package eu.trentorise.smartcampus.protocolcarrier.exceptions;

public class ProtocolException extends Exception {
	private static final long serialVersionUID = -4222758494637283953L;

	public ProtocolException(String message) {
		super(message);
	}

	public static String PROTOCOL_EXCEPTION = "PROTOCOL_EXCEPTION";
	
}
