package ai.aitia.arrowhead.it2generichttp;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.InterfaceModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;

@Component
public class InterfaceTranslatorToGenericHTTPSystemInfo extends SystemInfo {

	//=================================================================================================
	// members

	@Value(InterfaceTranslatorToGenericHTTPConstants.$ENABLE_AUTHORIZATION_WD)
	private boolean authorizationEnabled;

	@Value(InterfaceTranslatorToGenericHTTPConstants.$TOKEN_ENCRYPTION_KEY)
	private String tokenEncryptionKey;

	@Value(InterfaceTranslatorToGenericHTTPConstants.$TARGET_INTERFACE_SSL_ENABLED)
	private Boolean targetInterfaceSslEanbled;

	private SystemModel systemModel;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public SystemModel getSystemModel() {
		if (systemModel == null) {
			SystemModel.Builder builder = new SystemModel.Builder()
					.address(getAddress())
					.version(InterfaceTranslatorToGenericHTTPConstants.SYSTEM_VERSION);

			if (AuthenticationPolicy.CERTIFICATE == this.getAuthenticationPolicy()) {
				builder = builder.metadata(Constants.METADATA_KEY_X509_PUBLIC_KEY, getPublicKey());
			}

			systemModel = builder.build();
		}

		return systemModel;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public List<ServiceModel> getServices() {
		final List<String> fromInterfaces = List.of(
				isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME);
		final String toInterface = getTargetInterface();

		final ServiceModel interfaceBridgeManagement = new ServiceModel.Builder()
				.serviceDefinition(Constants.SERVICE_DEF_INTERFACE_BRIDGE_MANAGEMENT)
				.version(InterfaceTranslatorToGenericHTTPConstants.VERSION_INTERFACE_BRIDGE_MANAGEMENT)
				.metadata(Constants.METADATA_KEY_INTERFACE_BRIDGE, Map.of(
						Constants.METADATA_KEY_FROM, fromInterfaces,
						Constants.METADATA_KEY_TO, toInterface))
				.serviceInterface(getHTTPInterfaceForInterfaceBridgeManagement())
				.build();

		return List.of(interfaceBridgeManagement);
	}

	//-------------------------------------------------------------------------------------------------
	public String getTargetInterface() {
		if (targetInterfaceSslEanbled != null) {
			return targetInterfaceSslEanbled
					? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME
					: Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;
		}

		// if not defined specifically, then the system's SSL settings matters
		return isSslEnabled()
				? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME
				: Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;

	}

	//-------------------------------------------------------------------------------------------------
	public boolean shouldTokenUsed() {
		return AuthenticationPolicy.CERTIFICATE != getAuthenticationPolicy() && authorizationEnabled;
	}

	//-------------------------------------------------------------------------------------------------
	public String getTokenEncryptionKey() {
		return tokenEncryptionKey;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private InterfaceModel getHTTPInterfaceForInterfaceBridgeManagement() {
		final String templateName = getSslProperties().isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;

		final HttpOperationModel check = new HttpOperationModel.Builder()
				.method(HttpMethod.POST.name())
				.path(InterfaceTranslatorToGenericHTTPConstants.HTTP_API_OP_CHECK_TARGETS_PATH)
				.build();

		final HttpOperationModel init = new HttpOperationModel.Builder()
				.method(HttpMethod.POST.name())
				.path(InterfaceTranslatorToGenericHTTPConstants.HTTP_API_OP_INIT_BRIDGE_PATH)
				.build();

		final HttpOperationModel abort = new HttpOperationModel.Builder()
				.method(HttpMethod.DELETE.name())
				.path(InterfaceTranslatorToGenericHTTPConstants.HTTP_API_OP_ABORT_BRIDGE_PATH)
				.build();

		return new HttpInterfaceModel.Builder(templateName, getDomainAddress(), getServerPort())
				.basePath(InterfaceTranslatorToGenericHTTPConstants.HTTP_API_BRIDGE_MANAGEMENT_PATH)
				.operation(Constants.SERVICE_OP_INTERFACE_TRANSLATOR_CHECK_TARGETS, check)
				.operation(Constants.SERVICE_OP_INTERFACE_TRANSLATOR_INIT_BRIDGE, init)
				.operation(Constants.SERVICE_OP_INTERFACE_TRANSLATOR_ABORT_BRIDGE, abort)
				.build();
	}
}