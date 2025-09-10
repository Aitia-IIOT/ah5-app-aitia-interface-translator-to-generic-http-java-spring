package ai.aitia.arrowhead.it2generichttp.service.model;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;

@Component
public class BridgeStore {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	private Map<UUID, UUID> endpointToBridgeId = new ConcurrentHashMap<>();
	private Map<UUID, NormalizedTranslationBridgeModel> bridgeIdToModel = new ConcurrentHashMap<>();
	private Map<UUID, ZonedDateTime> bridgeIdToTimestamp = new ConcurrentHashMap<>();

	private static final Object LOCK = new Object();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void add(final NormalizedTranslationBridgeModel model) {
		logger.debug("BridgeStore.add started...");
		Assert.notNull(model, "model is null");

		synchronized (LOCK) {
			endpointToBridgeId.put(model.endpointId(), model.bridgeId());
			bridgeIdToModel.put(model.bridgeId(), model);
			bridgeIdToTimestamp.put(model.bridgeId(), Utilities.utcNow());
		}
	}

	//-------------------------------------------------------------------------------------------------
	public boolean containsEndpoint(final UUID endpointId) {
		logger.debug("BridgeStore.containsEndpoint started...");
		Assert.notNull(endpointId, "endpointId is null");

		synchronized (LOCK) {
			return endpointToBridgeId.containsKey(endpointId);
		}
	}

	//-------------------------------------------------------------------------------------------------
	public boolean containsBridgeId(final UUID bridgeId) {
		logger.debug("BridgeStore.containsBridgeId started...");
		Assert.notNull(bridgeId, "bridgeId is null");

		synchronized (LOCK) {
			return bridgeIdToModel.containsKey(bridgeId);
		}
	}

	//-------------------------------------------------------------------------------------------------
	public NormalizedTranslationBridgeModel getByBridgeId(final UUID bridgeId) {
		logger.debug("BridgeStore.getByBridgeId started...");
		Assert.notNull(bridgeId, "bridgeId is null");

		synchronized (LOCK) {
			return bridgeIdToModel.get(bridgeId);
		}
	}

	//-------------------------------------------------------------------------------------------------
	public NormalizedTranslationBridgeModel getByEndpointId(final UUID endpointId) {
		logger.debug("BridgeStore.getByEndpointId started...");
		Assert.notNull(endpointId, "endpointId is null");

		synchronized (LOCK) {
			final UUID bridgeId = endpointToBridgeId.get(endpointId);
			if (bridgeId != null) {
				final NormalizedTranslationBridgeModel result = bridgeIdToModel.get(bridgeId);
				if (result != null) {
					bridgeIdToTimestamp.put(bridgeId, Utilities.utcNow()); // new activity on the bridge
				}

				return result;
			}

			return null;
		}
	}
}
