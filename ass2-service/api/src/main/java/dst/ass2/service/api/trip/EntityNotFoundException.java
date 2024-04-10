package dst.ass2.service.api.trip;

/**
 * Exception indicating that a resource that was trying to be accessed does not exist.
 */
public class EntityNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
