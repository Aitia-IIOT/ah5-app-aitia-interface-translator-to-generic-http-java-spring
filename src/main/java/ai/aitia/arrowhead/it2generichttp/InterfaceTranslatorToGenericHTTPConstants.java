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
