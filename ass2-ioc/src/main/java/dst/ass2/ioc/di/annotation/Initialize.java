package dst.ass2.ioc.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Initialize marks a method to be invoked by the container after it has been constructed and all dependencies and
 * properties have been injected.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Initialize {

}
