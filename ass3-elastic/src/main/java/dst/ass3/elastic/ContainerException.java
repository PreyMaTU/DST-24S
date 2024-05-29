package dst.ass3.elastic;

/**
 * Exception indicating that the ContainerService encountered an error when performing a task.
 */
public class ContainerException extends Exception {

    public ContainerException() {
    }

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerException(Throwable cause) {
        super(cause);
    }
}
