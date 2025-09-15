package ai.aitia.arrowhead.it2generichttp.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPConstants;
import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPSystemInfo;
import eu.arrowhead.common.swagger.DefaultSwaggerConfig;
import io.swagger.v3.oas.models.info.License;
import jakarta.annotation.PostConstruct;

@Configuration
public class SwaggerConfig extends DefaultSwaggerConfig {

	//=================================================================================================
	// methods

	@Autowired
	private InterfaceTranslatorToGenericHTTPSystemInfo sysInfo;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public SwaggerConfig() {
		super(null, InterfaceTranslatorToGenericHTTPConstants.SYSTEM_VERSION);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected License apiLicence() {
		// TODO: add real licence here
		return super.apiLicence();
	}

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		setSystemName(sysInfo.getSystemName());
	}
}