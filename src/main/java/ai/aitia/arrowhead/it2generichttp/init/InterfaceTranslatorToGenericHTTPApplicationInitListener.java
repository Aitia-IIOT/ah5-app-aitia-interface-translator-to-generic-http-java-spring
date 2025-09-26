/*******************************************************************************
 *
 * Copyright (c) 2025 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 *
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  	AITIA
 *
 *******************************************************************************/
package ai.aitia.arrowhead.it2generichttp.init;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.Defaults;
import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPConstants;
import ai.aitia.arrowhead.it2generichttp.InterfaceTranslatorToGenericHTTPSystemInfo;
import ai.aitia.arrowhead.it2generichttp.report.ReportThread;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.init.ApplicationInitListener;
import eu.arrowhead.dto.AuthorizationEncryptionKeyRegistrationRequestDTO;
import eu.arrowhead.dto.AuthorizationGrantRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyResponseDTO;
import eu.arrowhead.dto.TranslationReportRequestDTO;
import eu.arrowhead.dto.enums.AuthorizationPolicyType;
import eu.arrowhead.dto.enums.AuthorizationTargetType;
import jakarta.annotation.Resource;

@Component
public class InterfaceTranslatorToGenericHTTPApplicationInitListener extends ApplicationInitListener {

	//=================================================================================================
	// members

	@Autowired
	private ReportThread reportThread;

	@Resource(name = InterfaceTranslatorToGenericHTTPConstants.REPORT_QUEUE)
	private BlockingQueue<TranslationReportRequestDTO> reportQueue;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
		logger.debug("customInit started...");

		final InterfaceTranslatorToGenericHTTPSystemInfo info = (InterfaceTranslatorToGenericHTTPSystemInfo) sysInfo;
		if (info.isAuthorizationEnabled()) {
			specifyAuthorizationPolicy();
		}

		if (info.shouldTokenUsed() && !Utilities.isEmpty(info.getTokenEncryptionKey())) {
			registerTokenEncryptionKey();
		}

		reportThread.start();
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customDestroy() {
		logger.debug("customDestroy started...");

		try {
			// send empty message to stop reporting thread gracefully
			reportQueue.put(new TranslationReportRequestDTO(null, null, null, null));
		} catch (final InterruptedException __) {
			// intentionally blank
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void specifyAuthorizationPolicy() {
		logger.debug("specifyAuthorizationPolicy started...");

		final AuthorizationGrantRequestDTO payload = new AuthorizationGrantRequestDTO(
				Defaults.DEFAULT_CLOUD,
				AuthorizationTargetType.SERVICE_DEF.name(),
				Constants.SERVICE_DEF_INTERFACE_BRIDGE_MANAGEMENT,
				"Only the Translation Manager should use this service",
				new AuthorizationPolicyRequestDTO(AuthorizationPolicyType.WHITELIST.name(), List.of(Constants.SYS_NAME_TRANSLATION_MANAGER), null),
				null);

		arrowheadHttpService.consumeService(
				Constants.SERVICE_DEF_AUTHORIZATION,
				Constants.SERVICE_OP_GRANT,
				AuthorizationPolicyResponseDTO.class,
				payload);
	}

	//-------------------------------------------------------------------------------------------------
	private void registerTokenEncryptionKey() {
		logger.debug("registerTokenEncryptionKey started...");

		final InterfaceTranslatorToGenericHTTPSystemInfo info = (InterfaceTranslatorToGenericHTTPSystemInfo) sysInfo;
		final AuthorizationEncryptionKeyRegistrationRequestDTO payload = new AuthorizationEncryptionKeyRegistrationRequestDTO(
				info.getTokenEncryptionKey(),
				InterfaceTranslatorToGenericHTTPConstants.AES_CBC_ALGORITHM_IV_BASED);
		final String initVector = arrowheadHttpService.consumeService(
				Constants.SERVICE_DEF_AUTHORIZATION_TOKEN,
				Constants.SERVICE_OP_AUTHORIZATION_TOKEN_REGISTER_ENCRYPTION_KEY,
				Constants.SYS_NAME_CONSUMER_AUTHORIZATION,
				String.class,
				payload);

		arrowheadContext.put(InterfaceTranslatorToGenericHTTPConstants.KEY_INITIALIZATION_VECTOR, initVector);
	}
}