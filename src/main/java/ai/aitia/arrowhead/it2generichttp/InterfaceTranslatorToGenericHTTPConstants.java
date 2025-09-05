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
 *  	AITIA - implementation
 *  	Arrowhead Consortia - conceptualization
 *
 *******************************************************************************/
package ai.aitia.arrowhead.it2generichttp;

public final class InterfaceTranslatorToGenericHTTPConstants {

	//=================================================================================================
	// members

	public static final String SYSTEM_NAME = "InterfaceTranslatorToGenericHTTP";
	public static final String SYSTEM_VERSION = "1.0.0";

	public static final String HTTP_API_BASE_PATH = "/interface/translator";
	public static final String HTTP_API_MONITOR_PATH = HTTP_API_BASE_PATH + "/monitor";
	public static final String HTTP_API_BRIDGE_MANAGEMENT_PATH = HTTP_API_BASE_PATH + "/bridge/mgmt";
	public static final String HTTP_PARAM_PATH_ID = "{pathId}";
	public static final String HTTP_API_DYNAMIC_PATH = HTTP_API_BASE_PATH + "/dynamic/" + HTTP_PARAM_PATH_ID;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private InterfaceTranslatorToGenericHTTPConstants() {
		throw new UnsupportedOperationException();
	}

}
