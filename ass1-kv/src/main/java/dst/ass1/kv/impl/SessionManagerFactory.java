package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.ISessionManagerFactory;

import java.util.Properties;

public class SessionManagerFactory implements ISessionManagerFactory {

    @Override
    public ISessionManager createSessionManager(Properties properties) {
        // TODO
        // read "redis.host" and "redis.port" from the properties

        return null;
    }
}
