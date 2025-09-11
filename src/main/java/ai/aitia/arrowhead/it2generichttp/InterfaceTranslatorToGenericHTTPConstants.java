package ai.aitia.arrowhead.it2generichttp;

public final class InterfaceTranslatorToGenericHTTPConstants {

	//=================================================================================================
	// members

	public static final String SYSTEM_NAME = "InterfaceTranslatorToGenericHTTP";
	public static final String SYSTEM_VERSION = "1.0.0";

	public static final String AES_CBC_ALGORITHM_IV_BASED = "AES/CBC/PKCS5Padding"; // With initialization vector
	public static final String KEY_INITIALIZATION_VECTOR = "authorization-initialization-vector";

	public static final String ENABLE_AUTHORIZATION = "enable.authorization";
	public static final String $ENABLE_AUTHORIZATION_WD = "${" + ENABLE_AUTHORIZATION + ":" + InterfaceTranslatorToGenericHTTPDefaults.ENABLE_AUTHORIZATION_DEFAULT + "}";
	public static final String TOKEN_ENCRYPTION_KEY = "token.encryption.key";
	public static final String $TOKEN_ENCRYPTION_KEY = "${" + TOKEN_ENCRYPTION_KEY + ":}";

	public static final String HTTP_API_BASE_PATH = "/interface/translator";
	public static final String HTTP_API_MONITOR_PATH = HTTP_API_BASE_PATH + "/monitor";
	public static final String HTTP_API_BRIDGE_MANAGEMENT_PATH = HTTP_API_BASE_PATH + "/bridge/mgmt";
	public static final String HTTP_API_OP_CHECK_TARGETS_PATH = "/check-targets";
	public static final String HTTP_API_OP_INIT_BRIDGE_PATH = "/initialize-bridge";
	public static final String HTTP_API_OP_ABORT_BRIDGE_PATH = "/abort-bridge";
	public static final String HTTP_PARAM_BRIDGE_ID = "{bridgeId}";
	public static final String HTTP_API_OP_ABORT_BRIDGE_PATH_WITH_PARAM = HTTP_API_OP_ABORT_BRIDGE_PATH + "/" + HTTP_PARAM_BRIDGE_ID;
	public static final String HTTP_PARAM_PATH_ID = "{pathId}";
	public static final String HTTP_API_DYNAMIC_PATH = HTTP_API_BASE_PATH + "/dynamic";
	public static final String HTTP_API_DYNAMIC_PATH_WITH_PARAM = HTTP_API_DYNAMIC_PATH  + "/" + HTTP_PARAM_PATH_ID;

	public static final String POLICY_TRANSLATION_BRIDGE_TOKEN = "TRANSLATION_BRIDGE_TOKEN";

	public static final String REPORT_QUEUE = "reportQueue";

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private InterfaceTranslatorToGenericHTTPConstants() {
		throw new UnsupportedOperationException();
	}
}