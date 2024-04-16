package dst.ass2.ioc.di;

public class InjectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InjectionException(String message) {
        super(message);
    }

    public InjectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InjectionException(Throwable cause) {
        super(cause);
    }

}
