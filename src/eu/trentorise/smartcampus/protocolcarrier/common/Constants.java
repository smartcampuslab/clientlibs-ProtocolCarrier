package eu.trentorise.smartcampus.protocolcarrier.common;

public class Constants {

	public static final int CODE_SECURITY_ERROR = 403;
	
	public static String USER_AGENT = "SmartCampus Android Client";
	public static String CHARSET = "utf-8";
	public static String URI_DEFAULT_SCHEME = "http";
	public static int URI_DEFAULT_PORT = 80;

	public enum Method {
		GET, POST, PUT, DELETE;
	}

	public enum RequestHeader {
		ACCEPT("Accept"), APP_TOKEN("APP_TOKEN"), AUTH_TOKEN("AUTH_TOKEN"), MESSAGEID("messageID");

		private String header;

		private RequestHeader(String s) {
			header = s;
		}

		@Override
		public String toString() {
			return header;
		}
	}

	public enum ResponseHeader {
		CONTENT_TYPE("Content-Type"), DATE("Date");

		private String header;

		private ResponseHeader(String s) {
			header = s;
		}

		@Override
		public String toString() {
			return header;
		}
	}

	public static String INTENT_EXTRA_ACTION = "action";
	public static String INTENT_EXTRA_MESSAGE_ID = "messageId";
	public static String INTENT_EXTRA_PAYLOAD = "payload";
	
	public static String INTENT_EXTRA_APP_TOKEN = "apptoken";
	
	public static String STATUS_SUCCESS = "SUCCESS";

}
