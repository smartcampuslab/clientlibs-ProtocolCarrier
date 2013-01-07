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

public class DeferredConf {

	private long requestTimeout = -1;
	private long responseTimeout = -1;

	public DeferredConf() {
	}

	public DeferredConf(Long requestTimeout, Long responseTimeout) {
		if (requestTimeout != null) {
			setRequestTimeout(requestTimeout);
		}

		if (responseTimeout != null) {
			setResponseTimeout(responseTimeout);
		}
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
