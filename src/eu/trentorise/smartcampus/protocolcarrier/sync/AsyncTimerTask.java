package eu.trentorise.smartcampus.protocolcarrier.sync;

import java.util.List;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import eu.trentorise.smartcampus.protocolcarrier.Communicator;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants;
import eu.trentorise.smartcampus.protocolcarrier.common.Status;
import eu.trentorise.smartcampus.protocolcarrier.custom.AsyncMessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.data.AsyncDbHelper;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.TimeoutException;

public class AsyncTimerTask extends TimerTask {

	private Context context;
	private String appToken;
	private boolean isEnabled = false;
	private Status status;
	private AsyncDbHelper dbHelper;

	private boolean isLocked = false;

	public AsyncTimerTask(Context context, String appToken, Status status) {
		this.context = context;
		this.appToken = appToken;
		this.status = status;
		dbHelper = new AsyncDbHelper(context);
	}

	@Override
	public void run() {
		// Get all asyncMessageRequests from db, process them, then do
		// that again. Maybe you can memorize the actions on the db and apply
		// all at the end of the cycle.
		if (isLocked) {
			return;
		} else {
			isLocked = true;
		}

		List<AsyncMessageRequest> list = dbHelper.getAllAsyncMessageRequest(appToken);

		for (AsyncMessageRequest aMsgRequest : list) {
			if (status.isOnline()) {
				String uuid = aMsgRequest.getUuid();
				MessageResponse msgResponse = null;
				boolean isExpired = isExpired(aMsgRequest);
				boolean runOnlyOnce = (aMsgRequest.getRequestTimeout() == 0);
				boolean isSuccessful = false;
				if (!isExpired) {
					try {
						msgResponse = Communicator.call(aMsgRequest.getMsgRequest(), aMsgRequest.getAppToken(),
								aMsgRequest.getAuthToken(), aMsgRequest.getResponseTimeout());
						isSuccessful = process(aMsgRequest, msgResponse, null);
					} catch (ConnectionException e) {
						Log.e(this.getClass().getName(), e.getMessage());
						isSuccessful = process(aMsgRequest, null, ConnectionException.CONNECTION_EXCEPTION);
					} catch (ProtocolException e) {
						Log.e(this.getClass().getName(), e.getMessage());
						isSuccessful = process(aMsgRequest, null, ProtocolException.PROTOCOL_EXCEPTION);
					} catch (SecurityException e) {
						Log.e(this.getClass().getName(), e.getMessage());
						isSuccessful = process(aMsgRequest, null, SecurityException.SECURITY_EXCEPTION);
					}
				} else {
					process(aMsgRequest, null, TimeoutException.TIMEOUT_EXCEPTION);
				}
				if (isExpired || runOnlyOnce || isSuccessful) {
					dbHelper.deleteAsyncMessageRequestByUuid(appToken, uuid);
				}
			}
		}

		isLocked = false;
	}

	private boolean process(AsyncMessageRequest aMsgRequest, MessageResponse msgResponse, String action) {
		boolean isSuccessful = true;

		if (aMsgRequest.getCallback() != null) {
			Intent intent = new Intent(aMsgRequest.getCallback());
			intent.putExtra(Constants.INTENT_EXTRA_MESSAGE_ID, aMsgRequest.getUuid());

			if (msgResponse == null) {
				intent.putExtra(Constants.INTENT_EXTRA_ACTION, action);
				isSuccessful = false;
			} else {
				// TODO: manage status? e.g.: 200 --> SUCCESS
				intent.putExtra(Constants.INTENT_EXTRA_ACTION, ((Integer) msgResponse.getHttpStatus()).toString());
				if (msgResponse.getBody() != null) {
					intent.putExtra(Constants.INTENT_EXTRA_PAYLOAD, msgResponse.getBody());
				}
			}

			context.sendBroadcast(intent);
		}

		return isSuccessful;
	}

	private boolean isExpired(AsyncMessageRequest aMsgRequest) {
		if (aMsgRequest.getRequestTimeout() == -1 || aMsgRequest.getRequestTimeout() == 0) {
			// timeout not set
			return false;
		} else if (aMsgRequest.getRequestTimeout() > 0
				&& aMsgRequest.getTimestamp() + aMsgRequest.getRequestTimeout() > System.currentTimeMillis()) {
			// timeout not reached
			return false;
		}

		return true;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

}
