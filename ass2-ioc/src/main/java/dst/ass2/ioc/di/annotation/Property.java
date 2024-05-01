package dst.ass2.ioc.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A property injects the value of a {@link java.util.Properties} object into a field based on a key.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

    /**
     * The key to look up in the container's Properties object.
     *
     * @return a key
     */
    String value();
}
