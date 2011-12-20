package example.jug.camel.logic;

public class NonRecoverableExternalServiceException extends
		ExternalServiceException {

	private static final long serialVersionUID = -1228073178517732546L;

	public NonRecoverableExternalServiceException() {
		super();
	}

	public NonRecoverableExternalServiceException(String message,
			Throwable cause) {
		super(message, cause);
	}

	public NonRecoverableExternalServiceException(String message) {
		super(message);
	}

	public NonRecoverableExternalServiceException(Throwable cause) {
		super(cause);
	}
}
