package ai.aitia.arrowhead.it2generichttp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPConstants;
import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPSystemInfo;
import ai.aitia.arrowhead.it2generichttp.service.model.BridgeStore;
import ai.aitia.arrowhead.it2generichttp.service.model.NormalizedTranslationBridgeModel;
import ai.aitia.arrowhead.it2generichttp.service.validation.ManagementServiceValidation;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.intf.properties.PropertyValidatorType;
import eu.arrowhead.common.intf.properties.PropertyValidators;
import eu.arrowhead.dto.ServiceInstanceInterfaceResponseDTO;
import eu.arrowhead.dto.TranslationBridgeInitializationRequestDTO;
import eu.arrowhead.dto.TranslationCheckTargetsRequestDTO;
import eu.arrowhead.dto.TranslationCheckTargetsResponseDTO;
import eu.arrowhead.dto.TranslationTargetDTO;

@Service
public class ManagementService {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private ManagementServiceValidation validator;

	@Autowired
	private PropertyValidators propertyValidators;

	@Autowired
	private BridgeStore bridgeStore;

	@Autowired
	private HttpEndpointHandler httpEndpointHandler;

	@Autowired
	private InterfaceTranslatorToGenericHTTPSystemInfo sysInfo;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public TranslationCheckTargetsResponseDTO checkTargetsOperation(final TranslationCheckTargetsRequestDTO dto, final String origin) {
		logger.debug("checkTargetsOperation started...");
		Assert.isTrue(!Utilities.isEmpty(origin), "origin is missing");

		final TranslationCheckTargetsRequestDTO normalized = validator.validateAndNormalizeTranslationCheckTargetsRequest(dto, origin);
		final List<TranslationTargetDTO> result = new ArrayList<>(normalized.targets().size());
		normalized.targets()
				.forEach(t -> {
					final List<ServiceInstanceInterfaceResponseDTO> appropriateInterfaces = new ArrayList<>();
					t.interfaces()
							.forEach(i -> {
								if (isAppropriateInterface(i, normalized.targetOperation())) {
									appropriateInterfaces.add(i);
								}
							});

					if (!Utilities.isEmpty(appropriateInterfaces)) {
						result.add(new TranslationTargetDTO(t.instanceId(), appropriateInterfaces));
					}
				});

		return new TranslationCheckTargetsResponseDTO(result);
	}

	//-------------------------------------------------------------------------------------------------
	public ServiceInstanceInterfaceResponseDTO initializeBridgeOperation(final TranslationBridgeInitializationRequestDTO dto, final String origin) {
		logger.debug("initializeBridgeOperation started...");
		Assert.isTrue(!Utilities.isEmpty(origin), "origin is missing");

		final NormalizedTranslationBridgeModel normalized = validator.validateAndNormalizeTranslationBridgeInitializationRequest(dto, origin);
		final EndpointHandler handler = getEndpointHandlerOfInterface(normalized.inputInterface());
		try {
			handler.initializeBridge(normalized);
			final ServiceInstanceInterfaceResponseDTO result = createInitializeBridgeResult(normalized);
			bridgeStore.add(normalized);

			return result;
		} catch (final InternalServerError ex) {
			throw new InternalServerError(ex.getMessage(), origin);
		} catch (final ExternalServerError ex) {
			throw new ExternalServerError(ex.getMessage(), origin);
		}
	}

	//-------------------------------------------------------------------------------------------------
	public boolean abortBridgeOperation(final String bridgeId, final String origin) {
		logger.debug("abortBridgeOperation started...");
		Assert.isTrue(!Utilities.isEmpty(origin), "origin is missing");

		final UUID normalized = validator.validateAndNormalizeBridgeId(bridgeId, origin);
		final NormalizedTranslationBridgeModel model = bridgeStore.removeByBridgeId(normalized);
		if (model != null) {
			final EndpointHandler handler = getEndpointHandlerOfInterface(model.inputInterface());
			handler.abortBridge(model);
		}

		return model != null;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private boolean isAppropriateInterface(final ServiceInstanceInterfaceResponseDTO intf, final String targetOperation) {
		logger.debug("isAppropriateInterface started...");

		if (sysInfo.getTargetInterface().equals(intf.templateName())) {
			switch (intf.templateName()) {
			case Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME:
			case Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME:
				return isAppropriateGenericHttpInterface(intf.properties(), targetOperation);
			default:
				// not supported interface => do nothing
			}
		}

		return false;
	}

	//-------------------------------------------------------------------------------------------------
	private boolean isAppropriateGenericHttpInterface(final Map<String, Object> properties, final String targetOperation) {
		logger.debug("isAppropriateGenericHttpInterface started...");

		try {
			// address
			if (!properties.containsKey(HttpInterfaceModel.PROP_NAME_ACCESS_ADDRESSES)) {
				return false;
			}
			propertyValidators.getValidator(PropertyValidatorType.NOT_EMPTY_ADDRESS_LIST).validateAndNormalize(properties.get(HttpInterfaceModel.PROP_NAME_ACCESS_ADDRESSES));

			// port
			if (!properties.containsKey(HttpInterfaceModel.PROP_NAME_ACCESS_PORT)) {
				return false;
			}
			propertyValidators.getValidator(PropertyValidatorType.PORT).validateAndNormalize(properties.get(HttpInterfaceModel.PROP_NAME_ACCESS_PORT));

			// base path
			if (!properties.containsKey(HttpInterfaceModel.PROP_NAME_BASE_PATH)) {
				return false;
			}

			if ((properties.get(HttpInterfaceModel.PROP_NAME_BASE_PATH) instanceof final String basePath)
					&& Utilities.isEmpty(basePath)) {
				return false;
			}

			// operations
			if (!properties.containsKey(HttpInterfaceModel.PROP_NAME_OPERATIONS)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			final Map<String, HttpOperationModel> normalized = (Map<String, HttpOperationModel>) propertyValidators.getValidator(PropertyValidatorType.HTTP_OPERATIONS)
					.validateAndNormalize(properties.get(HttpInterfaceModel.PROP_NAME_OPERATIONS));

			return normalized.containsKey(targetOperation);
		} catch (final InvalidParameterException __) {
			return false;
		}
	}

	//-------------------------------------------------------------------------------------------------
	private EndpointHandler getEndpointHandlerOfInterface(final String interfaceName) {
		logger.debug("getEndpointHandlerOfInterface started...");

		return switch (interfaceName) {
		case Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME, Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME -> httpEndpointHandler;
		default ->
			throw new InternalServerError("Interface " + interfaceName + " is not supported");
		};
	}

	//-------------------------------------------------------------------------------------------------
	private ServiceInstanceInterfaceResponseDTO createInitializeBridgeResult(final NormalizedTranslationBridgeModel model) {
		logger.debug("createInitializeBridgeResult started...");

		return new ServiceInstanceInterfaceResponseDTO(
				model.inputInterface(),
				calculateProtocol(model.inputInterface()),
				InterfaceTranslatorToGenericHTTPConstants.POLICY_TRANSLATION_BRIDGE_TOKEN,
				calculateInterfaceProperties(model));
	}

	//-------------------------------------------------------------------------------------------------
	private String calculateProtocol(final String interfaceName) {
		logger.debug("calculateProtocol started...");

		return switch (interfaceName) {
		case Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME -> Constants.HTTP;
		case Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME -> Constants.HTTPS;
		default ->
			throw new InternalServerError("Interface " + interfaceName + " is not supported");
		};
	}

	//-------------------------------------------------------------------------------------------------
	private Map<String, Object> calculateInterfaceProperties(final NormalizedTranslationBridgeModel model) {
		logger.debug("calculateInterfaceProperties started...");

		return switch (model.inputInterface()) {
		case Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME, Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME -> calculateInterfacePropertiesForGenericHTTP(model);
		default ->
			throw new InternalServerError("Interface " + model.inputInterface() + " is not supported");
		};
	}

	//-------------------------------------------------------------------------------------------------
	private Map<String, Object> calculateInterfacePropertiesForGenericHTTP(final NormalizedTranslationBridgeModel model) {
		logger.debug("calculateInterfacePropertiesForGenericHTTP started...");

		final HttpInterfaceModel intfModel = new HttpInterfaceModel.Builder(model.inputInterface())
				.accessAddress(sysInfo.getAddress())
				.accessPort(sysInfo.getServerPort())
				.basePath(InterfaceTranslatorToGenericHTTPConstants.HTTP_API_DYNAMIC_PATH)
				.operation(model.operation(), new HttpOperationModel.Builder()
						.method(HttpMethod.POST.name())
						.path("/" + model.endpointId())
						.build())
				.build();

		return intfModel.properties();
	}
}