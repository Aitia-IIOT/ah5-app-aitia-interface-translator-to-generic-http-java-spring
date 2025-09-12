package ai.aitia.arrowhead.it2generichttp.service.model;

import java.util.Map;
import java.util.UUID;

import eu.arrowhead.dto.TranslationDataModelTranslationDataDescriptorDTO;

public record NormalizedTranslationBridgeModel(
		UUID endpointId,
		UUID bridgeId,
		String inputInterface,
		TranslationDataModelTranslationDataDescriptorDTO inputDataModelTranslator,
		TranslationDataModelTranslationDataDescriptorDTO resultDataModelTranslator,
		String targetInterface,
		Map<String, Object> targetInterfaceProperties,
		String operation,
		String authorizationToken,
		Map<String, Object> interfaceTranslatorSettings) {
}