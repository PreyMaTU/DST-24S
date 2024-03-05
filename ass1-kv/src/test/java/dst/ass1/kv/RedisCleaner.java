package dst.ass1.kv;

import org.junit.rules.ExternalResource;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Drops the entire key space of a redis instance before and after a test run.
 */
public class RedisCleaner extends ExternalResource {

    private Properties properties;
    private Jedis jedis;

    @Override
    protected void before() throws IOException {
        properties = loadProperties();

        String host = properties.getProperty("redis.host");
        int port = Integer.parseInt(properties.getProperty("redis.port"));
        jedis = new Jedis(host, port);

        // completely clear the redis instance before each test
        jedis.flushAll();
    }

    @Override
    protected void after() {
        // completely clear the redis instance after each test
        try {
            jedis.flushAll();
        } finally {
            // close the connection pool
            jedis.close();
        }
    }

    /**
     * @return loaded redis properties
     */
    public Properties getProperties() {
        return properties;
    }

    public Jedis getJedis() {
        return jedis;
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("redis.properties")) {
            properties.load(in);
        }
        return properties;
    }

}
