package ai.aitia.arrowhead.it2generichttp.swagger;

import org.springframework.context.annotation.Configuration;

import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPConstants;
import eu.arrowhead.common.swagger.DefaultSwaggerConfig;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig extends DefaultSwaggerConfig {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public SwaggerConfig() {
		super(InterfaceTranslatorToGenericHTTPConstants.SYSTEM_NAME, InterfaceTranslatorToGenericHTTPConstants.SYSTEM_VERSION);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected License apiLicence() {
		// TODO: add real licence here
		return super.apiLicence();
	}
}