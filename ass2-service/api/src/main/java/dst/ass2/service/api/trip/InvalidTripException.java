package dst.ass2.service.api.trip;

public class InvalidTripException extends Exception {
    public InvalidTripException() {
    }

    public InvalidTripException(String message) {
        super(message);
    }
}
