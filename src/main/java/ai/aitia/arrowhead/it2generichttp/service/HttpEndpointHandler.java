package ai.aitia.arrowhead.it2generichttp.service;

import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.it2generichttp.service.model.NormalizedTranslationBridgeModel;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.InternalServerError;

@Service
public class HttpEndpointHandler implements EndpointHandler {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void initializeBridge(final NormalizedTranslationBridgeModel model) throws InternalServerError, ExternalServerError {
		// intentionally do nothing
	}
}