package eu.trentorise.smartcampus;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import eu.trentorise.smartcampus.common.Constants;
import eu.trentorise.smartcampus.common.PcIntents;
import eu.trentorise.smartcampus.custom.DeferredConf;
import eu.trentorise.smartcampus.custom.MessageRequest;
import eu.trentorise.smartcampus.custom.MessageResponse;
import eu.trentorise.smartcampus.data.AsyncDbHelper;
import eu.trentorise.smartcampus.exceptions.ConnectionException;
import eu.trentorise.smartcampus.exceptions.ProtocolException;
import eu.trentorise.smartcampus.sync.AsyncService;

public class ProtocolCarrier {

	private Context context;
	private AsyncDbHelper dbHelper;

	private final String APP_TOKEN;

	public ProtocolCarrier(Context context, String appToken) {
		this.context = context;
		this.APP_TOKEN = appToken;
		dbHelper = new AsyncDbHelper(context);

		this.context.startService(new Intent(this.context, AsyncService.class));
	}

	public MessageResponse invokeSync(MessageRequest msgRequest, String appToken, String authToken)
			throws ConnectionException, ProtocolException {
		if (msgRequest == null || appToken == null || authToken == null) {
			throw new IllegalArgumentException("msgRequest, appToken and authToken cannot be null", null);
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
		}

		return msgResponse;
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
		if (APP_TOKEN.equals(appToken)) {
			Intent intent = new Intent(PcIntents.INTENT_START);
			intent.putExtra(Constants.INTENT_EXTRA_APP_TOKEN, appToken);
			context.sendBroadcast(intent);
		} else {
			throw new ProtocolException("Async service not started.");
		}
	}

	// start async service
	public void stop(String appToken) throws ProtocolException {
		if (APP_TOKEN.equals(appToken)) {
			Intent intent = new Intent(PcIntents.INTENT_STOP);
			intent.putExtra(Constants.INTENT_EXTRA_APP_TOKEN, appToken);
			context.sendBroadcast(intent);
		} else {
			throw new ProtocolException("Async service not stopped.");
		}
	}

	// get all pending messages
	public Map<String, MessageRequest> getPendingMessages(String appToken) throws ProtocolException {
		if (APP_TOKEN.equals(appToken)) {
			return dbHelper.getAllMessageRequests(appToken);
		} else {
			throw new ProtocolException("Error retrieving pending message requests.");
		}
	}

	// remove all pending messages
	public void clear(String appToken) throws ProtocolException {
		if (APP_TOKEN.equals(appToken)) {
			dbHelper.deleteAsyncMessageRequests(appToken);
		} else {
			throw new ProtocolException("Error removing pending message requests.");
		}
	}

	// remove message by uuid
	public void removeMessage(String appToken, String uuid) throws ProtocolException {
		if (APP_TOKEN.equals(appToken)) {
			dbHelper.deleteAsyncMessageRequestByUuid(appToken, uuid);
		} else {
			throw new ProtocolException("Error removing request message with uuid " + uuid);
		}
	}

}
