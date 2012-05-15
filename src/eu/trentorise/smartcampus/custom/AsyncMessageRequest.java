package eu.trentorise.smartcampus.custom;

public class AsyncMessageRequest {

	private long timestamp;
	private String uuid;
	private MessageRequest msgRequest;
	private String appToken;
	private String authToken;
	private String callback;
	private long requestTimeout;
	private long responseTimeout;

	public AsyncMessageRequest(long timestamp, String uuid, MessageRequest msgRequest, String appToken,
			String authToken, String callback, long requestTimeout, long responseTimeout) {
		setTimestamp(timestamp);
		setUuid(uuid);
		setMsgRequest(msgRequest);
		setAppToken(appToken);
		setAuthToken(authToken);
		setCallback(callback);
		setRequestTimeout(requestTimeout);
		setResponseTimeout(responseTimeout);
	}

	public long getTimestamp() {
		return timestamp;
	}

	private void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUuid() {
		return uuid;
	}

	private void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public MessageRequest getMsgRequest() {
		return msgRequest;
	}

	private void setMsgRequest(MessageRequest msgRequest) {
		this.msgRequest = msgRequest;
	}

	public String getAppToken() {
		return appToken;
	}

	private void setAppToken(String appToken) {
		this.appToken = appToken;
	}

	public String getAuthToken() {
		return authToken;
	}

	private void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getCallback() {
		return callback;
	}

	private void setCallback(String callback) {
		this.callback = callback;
	}

	public long getRequestTimeout() {
		return requestTimeout;
	}

	private void setRequestTimeout(long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public long getResponseTimeout() {
		return responseTimeout;
	}

	private void setResponseTimeout(long responseTimeout) {
		this.responseTimeout = responseTimeout;
	}

}
