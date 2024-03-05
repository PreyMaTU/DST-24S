package dst.ass1.kv;

import java.util.Properties;

public interface ISessionManagerFactory {
    /**
     * Creates an implementation of an {@link ISessionManager}.
     *
     * @param properties the properties to use for instantiation
     * @return a session manager object
     */
    ISessionManager createSessionManager(Properties properties);
}
