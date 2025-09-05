package ai.aitia.arrowhead.it2generichttp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import ai.aitia.arrowhead.Constants;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan({ Constants.BASE_PACKAGE, Constants.COMMON_BASE_PACKAGE })
@EnableScheduling
public class InterfaceTranslatorToGenericHTTPMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(InterfaceTranslatorToGenericHTTPMain.class, args);
	}

	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	protected InterfaceTranslatorToGenericHTTPMain() {
	}
}