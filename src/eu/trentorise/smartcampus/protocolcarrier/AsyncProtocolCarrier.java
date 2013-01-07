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

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants;
import eu.trentorise.smartcampus.protocolcarrier.common.PcIntents;
import eu.trentorise.smartcampus.protocolcarrier.custom.DeferredConf;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.data.AsyncDbHelper;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.sync.AsyncService;

public class AsyncProtocolCarrier extends ProtocolCarrier {

	private AsyncDbHelper dbHelper;

	public AsyncProtocolCarrier(Context context, String appToken) {
		super(context,appToken);
		dbHelper = new AsyncDbHelper(context);
		this.mContext.startService(new Intent(this.mContext, AsyncService.class));
	}

	public String invokeAsync(MessageRequest msgRequest, String appToken, String authToken, String callback,
			DeferredConf deferredConf) {
		if (msgRequest == null || appToken == null || authToken == null) {
			throw new IllegalArgumentException("msgRequest, appToken and authToken cannot be null", null);
		}

		String messageID = dbHelper.addAsyncMessageRequest(msgRequest, appToken, authToken, callback, deferredConf);
		return messageID;
	}

	/*
	 * TODO: throw exceptions
	 */
	// start async service
	public void start(String appToken) throws ProtocolException {
		if (mAppToken.equals(appToken)) {
			Intent intent = new Intent(PcIntents.INTENT_START);
			intent.putExtra(Constants.INTENT_EXTRA_APP_TOKEN, appToken);
			mContext.sendBroadcast(intent);
		} else {
			throw new ProtocolException("Async service not started.");
		}
	}

	// start async service
	public void stop(String appToken) throws ProtocolException {
		if (mAppToken.equals(appToken)) {
			Intent intent = new Intent(PcIntents.INTENT_STOP);
			intent.putExtra(Constants.INTENT_EXTRA_APP_TOKEN, appToken);
			mContext.sendBroadcast(intent);
		} else {
			throw new ProtocolException("Async service not stopped.");
		}
	}

	// get all pending messages
	public Map<String, MessageRequest> getPendingMessages(String appToken) throws ProtocolException {
		if (mAppToken.equals(appToken)) {
			return dbHelper.getAllMessageRequests(appToken);
		} else {
			throw new ProtocolException("Error retrieving pending message requests.");
		}
	}

	// remove all pending messages
	public void clear(String appToken) throws ProtocolException {
		if (mAppToken.equals(appToken)) {
			dbHelper.deleteAsyncMessageRequests(appToken);
		} else {
			throw new ProtocolException("Error removing pending message requests.");
		}
	}

	// remove message by uuid
	public void removeMessage(String appToken, String uuid) throws ProtocolException {
		if (mAppToken.equals(appToken)) {
			dbHelper.deleteAsyncMessageRequestByUuid(appToken, uuid);
		} else {
			throw new ProtocolException("Error removing request message with uuid " + uuid);
		}
	}

}
