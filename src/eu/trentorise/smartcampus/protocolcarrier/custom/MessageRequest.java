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

import java.util.List;

import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;

public class MessageRequest {
	private Method method = Method.GET; // not required
	private String targetHost;
	private String targetAddress;
	private String body = null; // not required
	// private byte[] fileContent = null;
	private List<RequestParam> requestParams;
	private boolean requestFile = false;
	private String query; // not required
	private String contentType = "application/json"; // not required

	public MessageRequest(String targetHost, String targetAddress) {
		if (targetHost == null || targetAddress == null) {
			throw new IllegalArgumentException(
					"targetHost and targetAddress cannot be null", null);
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

	// public byte[] getFileContent() {
	// return fileContent;
	// }
	//
	// public void setFileContent(byte[] fileContent) {
	// this.fileContent = fileContent;
	// }

	public boolean isRequestFile() {
		return requestFile;
	}

	public void setRequestFile(boolean requestFile) {
		this.requestFile = requestFile;
	}

	public List<RequestParam> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(List<RequestParam> requestParams) {
		this.requestParams = requestParams;
	}
}
