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

	private SystemModel systemModel;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public String getSystemName() {
		return InterfaceTranslatorToGenericHTTPConstants.SYSTEM_NAME;
	}

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
	public boolean shouldTokenUsed() {
		return AuthenticationPolicy.CERTIFICATE != getAuthenticationPolicy() && authorizationEnabled;
	}

	//-------------------------------------------------------------------------------------------------
	public String getTokenEncryptionKey() {
		return tokenEncryptionKey;
	}
}