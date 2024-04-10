package dst.ass2.service.api.trip;

public class DriverNotAvailableException extends Exception {

    public DriverNotAvailableException(String message) {
        super(message);
    }

    public DriverNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
