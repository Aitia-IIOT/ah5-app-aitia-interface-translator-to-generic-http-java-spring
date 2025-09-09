package ai.aitia.arrowhead.it2generichttp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ai.aitia.arrowhead.it2generichttp.service.validation.ManagementServiceValidation;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.dto.TranslationCheckTargetsRequestDTO;
import eu.arrowhead.dto.TranslationCheckTargetsResponseDTO;

@Service
public class ManagementService {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private ManagementServiceValidation validator;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public TranslationCheckTargetsResponseDTO checkTargetsOperation(final TranslationCheckTargetsRequestDTO dto, final String origin) {
		logger.debug("checkTargetsOperation started...");
		Assert.isTrue(!Utilities.isEmpty(origin), "origin is missing");
	
		final TranslationCheckTargetsRequestDTO normalized = validator.validateAndNormalizeTranslationCheckTargetsRequest(dto, origin);
		
		// TODO: continue
		
		return null;
	}
}