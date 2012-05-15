package eu.trentorise.smartcampus.custom;

import eu.trentorise.smartcampus.common.Constants.Method;

public class MessageRequest {
	private Method method = Method.GET; // not required
	private String targetHost;
	private String targetAddress;
	private String body = null; // not required
	private String query; // not required
	private String contentType = "application/json"; // not required

	public MessageRequest(String targetHost, String targetAddress) {
		if (targetHost == null || targetAddress == null) {
			throw new IllegalArgumentException("targetHost and targetAddress cannot be null", null);
		}

		setTargetHost(targetHost);
		setTargetAddress(targetAddress);
	}

	/*
	 * Getters and setters
	 */
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public String getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}