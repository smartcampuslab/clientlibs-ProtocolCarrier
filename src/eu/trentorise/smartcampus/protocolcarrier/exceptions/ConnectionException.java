package eu.trentorise.smartcampus.protocolcarrier.exceptions;

public class ConnectionException extends Exception {
	private static final long serialVersionUID = 2838173241739443633L;

	public ConnectionException(String message) {
		super(message);
	}
	
	public static String CONNECTION_EXCEPTION = "CONNECTION_EXCEPTION";
	
}
