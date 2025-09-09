package ai.aitia.arrowhead.it2generichttp.report;

import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.http.ArrowheadHttpService;
import eu.arrowhead.dto.TranslationReportRequestDTO;
import jakarta.annotation.Resource;

@Component
public class ReportThread extends Thread {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Resource(name = InterfaceTranslatorToGenericHTTPConstants.REPORT_QUEUE)
	private BlockingQueue<TranslationReportRequestDTO> queue;

	@Autowired
	private ArrowheadHttpService httpService;

	private boolean doWork = true;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		logger.debug("ReportThread.run started...");

		while (doWork) {
			try {
				final TranslationReportRequestDTO request = queue.take();
				handleRequest(request);
			} catch (final Throwable t) {
				logger.error(t.getMessage());
				logger.debug(t);
			}
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void handleRequest(final TranslationReportRequestDTO request) {
		logger.debug("ReportThread.handleRequest started...");

		if (!Utilities.isEmpty(request.bridgeId())) {
			httpService.consumeService(
					Constants.SERVICE_DEF_TRANSLATION_REPORT,
					Constants.SERVICE_OP_REPORT,
					Constants.SYS_NAME_TRANSLATIONMANAGER,
					Void.TYPE,
					request);
		} else {
			// stopping request
			doWork = false;
		}
	}
}