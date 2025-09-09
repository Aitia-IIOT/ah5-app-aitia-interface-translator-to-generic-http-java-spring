package ai.aitia.arrowhead.it2generichttp.service.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.service.validation.name.ServiceOperationNameNormalizer;
import eu.arrowhead.dto.TranslationCheckTargetsRequestDTO;
import eu.arrowhead.dto.TranslationTargetDTO;

@Service
public class ManagementServiceValidation {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private ServiceOperationNameNormalizer serviceOpNormalizer;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	// VALIDATION AND NORMALIZATION

	//-------------------------------------------------------------------------------------------------
	public TranslationCheckTargetsRequestDTO validateAndNormalizeTranslationCheckTargetsRequest(final TranslationCheckTargetsRequestDTO dto, final String origin) {
		logger.debug("validateAndNormalizeTranslationCheckTargetsRequest started...");
		Assert.isTrue(!Utilities.isEmpty(origin), "origin is missing");

		validateTranslationCheckTargetsRequest(dto, origin);
		final TranslationCheckTargetsRequestDTO normalized = normalizedTranslationCheckTargetsRequest(dto);

		// TODO: continue

		return null;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	// VALIDATION

	//-------------------------------------------------------------------------------------------------
	private void validateTranslationCheckTargetsRequest(final TranslationCheckTargetsRequestDTO dto, final String origin) {
		logger.debug("validateTranslationCheckTargetsRequest started...");

		if (dto == null) {
			throw new InvalidParameterException("Request is missing", origin);
		}

		if (Utilities.isEmpty(dto.targetOperation())) {
			throw new InvalidParameterException("Target operation is missing", origin);
		}

		if (Utilities.isEmpty(dto.targets())) {
			throw new InvalidParameterException("targets list is missing", origin);
		}

		if (Utilities.containsNull(dto.targets())) {
			throw new InvalidParameterException("targets list contains null element", origin);
		}
	}

	//-------------------------------------------------------------------------------------------------
	// NORMALIZATION

	//-------------------------------------------------------------------------------------------------
	private TranslationCheckTargetsRequestDTO normalizedTranslationCheckTargetsRequest(final TranslationCheckTargetsRequestDTO dto) {
		logger.debug("normalizedTranslationCheckTargetsRequest started...");

		return new TranslationCheckTargetsRequestDTO(
				serviceOpNormalizer.normalize(dto.targetOperation()),
				dto.targets()
						.stream()
						.map(t -> normalizeTarget(t))
						.toList());
	}

	//-------------------------------------------------------------------------------------------------
	private TranslationTargetDTO normalizeTarget(final TranslationTargetDTO target) {
		logger.debug("normalizeTarget started...");
		
		// TODO: continue from here
		
		return null;
	}
}