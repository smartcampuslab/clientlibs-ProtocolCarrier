package eu.trentorise.smartcampus.exceptions;

public class SecurityException extends Exception {
	private static final long serialVersionUID = -3615997721790141057L;

	public SecurityException(String message) {
		super(message);
	}

	public static String SECURITY_EXCEPTION = "SECURITY_EXCEPTION";
	
}
