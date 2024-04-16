package dst.ass2.ioc.di;

import dst.ass2.ioc.di.annotation.Property;

import java.util.Properties;

public interface IObjectContainerFactory {

    /**
     * Creates a new IObjectContainer instance that uses the given properties for injecting
     * {@link Property} instances.
     *
     * @param properties a Properties object
     * @return a new IObjectContainer instance
     */
    IObjectContainer newObjectContainer(Properties properties);
}
