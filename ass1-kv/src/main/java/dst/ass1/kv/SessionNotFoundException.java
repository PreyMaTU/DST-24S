package dst.ass1.kv;

/**
 * Exception to indicate that <i>this is not the session you are looking for</i>.
 */
public class SessionNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public SessionNotFoundException() {
    }

    public SessionNotFoundException(String message) {
        super(message);
    }

    public SessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionNotFoundException(Throwable cause) {
        super(cause);
    }
}
