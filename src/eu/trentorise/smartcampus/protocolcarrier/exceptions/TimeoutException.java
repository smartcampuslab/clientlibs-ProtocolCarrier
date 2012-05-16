package eu.trentorise.smartcampus.protocolcarrier.exceptions;

public class TimeoutException extends Exception {
	private static final long serialVersionUID = -5659291078546971526L;

	public TimeoutException(String message) {
		super(message);
	}

	public static String TIMEOUT_EXCEPTION = "TIMEOUT_EXCEPTION";
	
}
