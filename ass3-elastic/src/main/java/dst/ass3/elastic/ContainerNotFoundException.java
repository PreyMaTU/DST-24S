package dst.ass3.elastic;

/**
 * Indicates that a container could not be found.
 */
public class ContainerNotFoundException extends ContainerException {

    public ContainerNotFoundException() {
    }

    public ContainerNotFoundException(String message) {
        super(message);
    }

    public ContainerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerNotFoundException(Throwable cause) {
        super(cause);
    }

}
