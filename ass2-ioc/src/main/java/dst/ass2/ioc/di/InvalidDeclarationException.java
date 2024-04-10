package dst.ass2.ioc.di;

public class InvalidDeclarationException extends InjectionException {
    public InvalidDeclarationException(String message) {
        super(message);
    }

    public InvalidDeclarationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDeclarationException(Throwable cause) {
        super(cause);
    }
}
