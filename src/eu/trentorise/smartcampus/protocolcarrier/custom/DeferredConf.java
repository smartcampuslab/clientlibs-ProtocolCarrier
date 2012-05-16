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
