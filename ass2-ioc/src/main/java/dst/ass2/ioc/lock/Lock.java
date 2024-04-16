package dst.ass2.ioc.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be executed by acquiring a named lock.
 */
// TODO: add correct retention policy and target type
public @interface Lock {

    /**
     * The name of the container managed lock.
     *
     * @return a name
     */
    String value();

}
