package eu.trentorise.smartcampus.protocolcarrier.common;


public class Setup {

	public static boolean DEBUG = false;

	public static String DB_NAME = "ProtocolCarrier";
	public static int DB_VERSION = 1;

	public static long TIMERTASK_ASYNC_DELAY = 0;
	public static long TIMERTASK_ASYNC_PERIOD = 2000;

	/*
	 * DB
	 */
	public static String DB_ASYNC_TABLE = "async";
	public static String DB_ASYNC_COL_TIMESTAMP = "timestamp";
	public static String DB_ASYNC_COL_UUID = "uuid";
	public static String DB_ASYNC_COL_METHOD = "method";
	public static String DB_ASYNC_COL_TARGETHOST = "targethost";
	public static String DB_ASYNC_COL_TARGETADDRESS = "targetaddress";
	public static String DB_ASYNC_COL_BODY = "body";
	public static String DB_ASYNC_COL_QUERY = "query";
	public static String DB_ASYNC_COL_CONTENTTYPE = "contettype";
	public static String DB_ASYNC_COL_APP_TOKEN = "apptoken";
	public static String DB_ASYNC_COL_AUTH_TOKEN = "authtoken";
	public static String DB_ASYNC_COL_CALLBACK = "callback";
	public static String DB_ASYNC_COL_REQUEST_TIMEOUT = "requesttimeout";
	public static String DB_ASYNC_COL_RESPONSE_TIMEOUT = "responsetimeout";

}
