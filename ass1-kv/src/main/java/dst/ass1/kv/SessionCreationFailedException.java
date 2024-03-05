package dst.ass1.kv;

/**
 * Exception indicating that a request to create a new session could not be fulfilled.
 */
public class SessionCreationFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public SessionCreationFailedException() {
    }

    public SessionCreationFailedException(String message) {
        super(message);
    }

    public SessionCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionCreationFailedException(Throwable cause) {
        super(cause);
    }

}
