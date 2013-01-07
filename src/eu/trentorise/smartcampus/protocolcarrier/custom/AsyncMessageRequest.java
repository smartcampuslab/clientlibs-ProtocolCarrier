/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.protocolcarrier.custom;

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
