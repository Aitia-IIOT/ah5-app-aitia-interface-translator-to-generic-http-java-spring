package ai.aitia.arrowhead.it2generichttp.quartz;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPConstants;
import ai.aitia.arrowhead.it2generichttp.service.model.BridgeStore;
import ai.aitia.arrowhead.it2generichttp.service.model.NormalizedTranslationBridgeModel;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.http.ArrowheadHttpService;
import eu.arrowhead.dto.IdentityLoginResponseDTO;
import eu.arrowhead.dto.IdentityRequestDTO;
import eu.arrowhead.dto.TranslationReportRequestDTO;
import eu.arrowhead.dto.enums.TranslationBridgeEventState;
import jakarta.annotation.Resource;

@Component
@DisallowConcurrentExecution
public class BridgeClosingJob implements Job {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value(InterfaceTranslatorToGenericHTTPConstants.$BRIDGE_INACTIVITY_THRESHOLD_WD)
	private int threshold;

	@Autowired
	private BridgeStore bridgeStore;

	@Resource(name = InterfaceTranslatorToGenericHTTPConstants.REPORT_QUEUE)
	private BlockingQueue<TranslationReportRequestDTO> reportQueue;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		logger.debug("Login job called...");

		try {
			final ZonedDateTime now = Utilities.utcNow();
			final ZonedDateTime thresholdTime = now.minusMinutes(threshold);
			final List<NormalizedTranslationBridgeModel> list = bridgeStore.getBridgeModelsWithOlderActivityThan(thresholdTime);
			list.forEach(model -> {
				bridgeStore.removeByBridgeId(model.bridgeId());
				sendClosedReport(model);
			});
		} catch (final Exception ex) {
			logger.error(ex.getMessage());
			logger.debug(ex);
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void sendClosedReport(final NormalizedTranslationBridgeModel model) {
		logger.debug("sendClosedReport started...");

		final TranslationReportRequestDTO report = new TranslationReportRequestDTO(
				model.bridgeId().toString(),
				Utilities.convertZonedDateTimeToUTCString(Utilities.utcNow()),
				TranslationBridgeEventState.INTERNAL_CLOSED.name(),
				null);

		reportQueue.add(report);
	}
}