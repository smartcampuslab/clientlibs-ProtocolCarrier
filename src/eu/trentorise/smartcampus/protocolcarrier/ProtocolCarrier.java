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
		} catch (SecurityException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			throw e;
		}

		return msgResponse;
	}

}
