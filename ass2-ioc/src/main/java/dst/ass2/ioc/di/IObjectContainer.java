package dst.ass2.ioc.di;

import dst.ass2.ioc.di.annotation.Property;

import java.util.Properties;

public interface IObjectContainer {

    /**
     * Returns the (mutable) data structure holding property values that can be injected via the
     * {@link Property} annotation.
     *
     * @return A mutable Properties object
     */
    Properties getProperties();

    /**
     * Returns a container-managed object of the given type.
     *
     * @param type the type of object
     * @param <T>  the class type
     * @return the object
     * @throws InjectionException throw the concrete InjectionException as specified in the assignment
     */
    <T> T getObject(Class<T> type) throws InjectionException;

}
