package dst.ass3.elastic;

/**
 * Exception indicating that the image which should be used for a container start is not available.
 */
public class ImageNotFoundException extends ContainerException {

    public ImageNotFoundException() {
    }

    public ImageNotFoundException(String message) {
        super(message);
    }

    public ImageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageNotFoundException(Throwable cause) {
        super(cause);
    }
}
