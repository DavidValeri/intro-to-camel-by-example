package example.jug.camel.logic;

public class ExternalServiceException extends Exception {
	
	private static final long serialVersionUID = 4280829624030840007L;

	public ExternalServiceException() {
		super();
	}

	public ExternalServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExternalServiceException(String message) {
		super(message);
	}

	public ExternalServiceException(Throwable cause) {
		super(cause);
	}
}
