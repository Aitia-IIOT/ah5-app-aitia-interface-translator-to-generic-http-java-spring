package ai.aitia.arrowhead.it2generichttp.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPConstants;
import ai.aitia.arrowhead.it2generichttp.service.engine.DataModelTranslatorEngine;
import ai.aitia.arrowhead.it2generichttp.service.engine.ProviderDriver;
import ai.aitia.arrowhead.it2generichttp.service.model.BridgeStore;
import ai.aitia.arrowhead.it2generichttp.service.model.NormalizedTranslationBridgeModel;
import ai.aitia.arrowhead.it2generichttp.service.validation.DynamicServiceValidation;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.dto.TranslationReportRequestDTO;
import eu.arrowhead.dto.enums.TranslationBridgeEventState;
import jakarta.annotation.Resource;

@Service
public class DynamicService {

	//=================================================================================================
	// members

	private static final String ABORT_MSG = "Translation bridge is aborted";

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private DynamicServiceValidation validator;

	@Autowired
	private BridgeStore bridgeStore;

	@Resource(name = InterfaceTranslatorToGenericHTTPConstants.REPORT_QUEUE)
	private BlockingQueue<TranslationReportRequestDTO> reportQueue;

	@Autowired
	private DataModelTranslatorEngine dmEngine;

	@Autowired
	private ProviderDriver providerDriver;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Pair<Integer, Optional<String>> doBridgeOperation(final String endpointId, final String payloadBase64, final String origin) {
		logger.debug("doBridgeOperation started...");
		Assert.isTrue(!Utilities.isEmpty(origin), "origin is missing");

		final UUID normalized = validator.validateAndNormalizeEndpointId(endpointId, origin);
		final NormalizedTranslationBridgeModel model = bridgeStore.getByEndpointId(normalized);

		// invalid target
		if (model == null) {
			throw new InvalidParameterException("Request target is invalid", origin);
		}

		validator.crossCheckModelAndPayload(model, payloadBase64, origin);

		// send report
		sendReport(model, TranslationBridgeEventState.USED, null);

		try {
			// translate payload if necessary
			final byte[] input = handleInputPayload(model, payloadBase64);

			// checking if bridge is still exists
			if (!bridgeStore.containsBridgeId(model.bridgeId())) {
				throw new ExternalServerError("Translation bridge is aborted");
			}

			// calling the target operation
			final Pair<Integer, Optional<byte[]>> response = providerDriver.callOperation(
					model.operation(),
					model.targetInterface(),
					model.targetInterfaceProperties(),
					input,
					model.authorizationToken());

			validator.crossCheckModelAndResult(model, response.getSecond(), origin);

			// translate result if necessary
			final String result = handleResult(model, response.getSecond());

			return Pair.of(
					response.getFirst(),
					Utilities.isEmpty(result) ? Optional.empty() : Optional.of(result));
		} catch (final ExternalServerError ex) {
			if (!ABORT_MSG.equals(ex.getMessage())) {
				sendReport(model, TranslationBridgeEventState.EXTERNAL_ERROR, ex.getMessage());
			}

//			bridgeStore.removeByBridgeId(model.bridgeId()); // TODO: uncomment this
			throw ex;
		} catch (final Exception ex) {
			sendReport(model, TranslationBridgeEventState.INTERNAL_ERROR, ex.getMessage());

//			bridgeStore.removeByBridgeId(model.bridgeId()); // TODO: uncomment this
			throw ex;
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void sendReport(final NormalizedTranslationBridgeModel model, final TranslationBridgeEventState state, final String message) {
		logger.debug("sendReport started...");

		final TranslationReportRequestDTO report = new TranslationReportRequestDTO(
				model.bridgeId().toString(),
				Utilities.convertZonedDateTimeToUTCString(Utilities.utcNow()),
				state.name(),
				message);

		reportQueue.add(report);
	}

	//-------------------------------------------------------------------------------------------------
	private byte[] handleInputPayload(final NormalizedTranslationBridgeModel model, final String payloadBase64) {
		logger.debug("handleInputPayload started...");

		if (payloadBase64 == null) {
			return null;
		}

		String input = payloadBase64;
		if (model.inputDataModelTranslator() != null) {
			input = dmEngine.translate(
					model.bridgeId(),
					model.inputDataModelTranslator(),
					input,
					model.interfaceTranslatorSettings());
		}

		return Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8));
	}

	//-------------------------------------------------------------------------------------------------
	private String handleResult(final NormalizedTranslationBridgeModel model, final Optional<byte[]> result) {
		logger.debug("handleResult started...");

		if (result.isEmpty() || result.get().length == 0) {
			return null;
		}

		String output = new String(Base64.getEncoder().encode(result.get()), StandardCharsets.UTF_8);
		if (model.resultDataModelTranslator() != null) {
			// checking if bridge is still exists
			if (!bridgeStore.containsBridgeId(model.bridgeId())) {
				throw new ExternalServerError("Translation bridge is aborted");
			}

			output = dmEngine.translate(
					model.bridgeId(),
					model.resultDataModelTranslator(),
					output,
					model.interfaceTranslatorSettings());
		}

		return output;
	}
}