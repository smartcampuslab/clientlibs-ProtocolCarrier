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
package eu.trentorise.smartcampus.protocolcarrier;

import android.content.Context;
import android.util.Log;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class ProtocolCarrier {

	protected Context mContext;
	protected final String mAppToken;

	public ProtocolCarrier(Context context, String appToken) {
		this.mContext = context;
		this.mAppToken = appToken;
	}

	public MessageResponse invokeSync(MessageRequest msgRequest, String appToken, String authToken)
			throws ConnectionException, ProtocolException, SecurityException {
		if (msgRequest == null) {
			throw new IllegalArgumentException("msgRequest cannot be null", null);
		}

		MessageResponse msgResponse = null;

		try {
			msgResponse = Communicator.call(msgRequest, appToken, authToken, null);
		} catch (ConnectionException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			throw e;
		} catch (ProtocolException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			throw e;
		} catch (SecurityException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			throw e;
		}

		return msgResponse;
	}

}
