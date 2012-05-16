package eu.trentorise.smartcampus.protocolcarrier.common;

import android.content.Context;

public class Status {

	private boolean isOnline;

	public Status(Context context) {
		setOnline(Utils.isOnline(context));
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

}
