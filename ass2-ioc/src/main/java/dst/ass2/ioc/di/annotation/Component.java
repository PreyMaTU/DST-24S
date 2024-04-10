package dst.ass2.ioc.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class to be container managed.
 */
// TODO: add correct retention policy and target type
public @interface Component {

    /**
     * The component can either be marked to be a singleton or a prototype via the {@link Scope} enum. The default scope
     * is singleton.
     *
     * @return the scope
     */
    Scope scope() default Scope.SINGLETON;
}
