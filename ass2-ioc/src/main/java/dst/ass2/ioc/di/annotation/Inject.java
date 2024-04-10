package dst.ass2.ioc.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inject marks a field in a class to be autowired by the container.
 */
// TODO: add correct retention policy and target type
public @interface Inject {

    /**
     * Marks whether the dependency is required or not.
     *
     * @return a boolean value
     */
    boolean optional() default false;

    /**
     * Explicitly states the class that should be injected by the container into this field. If the target type is Void,
     * then we assume the targetType is not set, and the container uses the declared field type instead.
     *
     * @return a class
     */
    Class<?> targetType() default Void.class;
}
