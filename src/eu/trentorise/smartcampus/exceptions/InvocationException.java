package eu.trentorise.smartcampus.exceptions;

public class InvocationException extends Exception {
	private static final long serialVersionUID = -1793506422727248081L;

	public InvocationException(String message) {
		super(message);
	}

	public static String INVOCATION_EXCEPTION = "INVOCATION_EXCEPTION";
	
}
