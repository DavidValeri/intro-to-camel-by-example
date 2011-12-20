package example.jug.camel.logic;

public class RecoverableExternalServiceException extends
		ExternalServiceException {

	private static final long serialVersionUID = -7367488595909724711L;

	public RecoverableExternalServiceException() {
		super();
	}

	public RecoverableExternalServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecoverableExternalServiceException(String message) {
		super(message);
	}

	public RecoverableExternalServiceException(Throwable cause) {
		super(cause);
	}
}
