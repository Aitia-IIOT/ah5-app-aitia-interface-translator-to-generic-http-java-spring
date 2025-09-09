package ai.aitia.arrowhead.it2generichttp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.arrowhead.dto.TranslationReportRequestDTO;

@Configuration
public class BeanConfig {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Bean(InterfaceTranslatorToGenericHTTPConstants.REPORT_QUEUE)
	BlockingQueue<TranslationReportRequestDTO> getReportQueue() {
		return new LinkedBlockingQueue<>();
	}
}