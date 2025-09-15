package ai.aitia.arrowhead.it2generichttp;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
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
		// TODO implement
		return List.of();
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
}