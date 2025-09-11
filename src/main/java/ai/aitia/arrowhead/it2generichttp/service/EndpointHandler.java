package ai.aitia.arrowhead.it2generichttp.service;

import ai.aitia.arrowhead.it2generichttp.service.model.NormalizedTranslationBridgeModel;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.InternalServerError;

public interface EndpointHandler {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void initializeBridge(final NormalizedTranslationBridgeModel model) throws InternalServerError, ExternalServerError;

	//-------------------------------------------------------------------------------------------------
	public void abortBridge(final NormalizedTranslationBridgeModel model) throws InternalServerError, ExternalServerError;
}