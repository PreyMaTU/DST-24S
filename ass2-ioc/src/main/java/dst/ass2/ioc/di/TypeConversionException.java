package dst.ass2.ioc.di;

public class TypeConversionException extends InjectionException {
    public TypeConversionException(String message) {
        super(message);
    }

    public TypeConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeConversionException(Throwable cause) {
        super(cause);
    }
}
