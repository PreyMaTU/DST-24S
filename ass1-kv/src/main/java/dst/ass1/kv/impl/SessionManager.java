package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.SessionCreationFailedException;
import dst.ass1.kv.SessionNotFoundException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Properties;
import java.util.UUID;

public class SessionManager implements ISessionManager {
    private static final String USER_ID_PREFIX= "user#";
    private static final String SESSION_ID_PREFIX= "session#";
    private final JedisPool jedisPool;

    SessionManager(Properties properties) {
        final var host= properties.getProperty("redis.host");
        final var port= Integer.parseInt( properties.getProperty("redis.port") );
        this.jedisPool= new JedisPool( host, port );
    }

    @Override
    public String createSession(Long userId, int timeToLive) throws SessionCreationFailedException {
        try (final var jedis = jedisPool.getResource()) {
            return createSessionWithJedis( jedis, userId, timeToLive );
        }
    }

    private String createSessionWithJedis(Jedis jedis, Long userId, int timeToLive ) throws SessionCreationFailedException {
        // Create new session token
        final var sessionToken = UUID.randomUUID().toString();
        final var userIdKey = USER_ID_PREFIX + userId;
        final var sessionKey = SESSION_ID_PREFIX + sessionToken;

        // Start atomic transaction
        jedis.watch( userIdKey, sessionKey );
        final var transaction = jedis.multi();

        // Save the session token on the user id
        transaction.set(userIdKey, sessionToken);
        transaction.expire(userIdKey, timeToLive);

        // Save the user id and TTL on the session token map
        transaction.hset(sessionKey, "userId", Long.toString(userId));
        transaction.hset(sessionKey, "timeToLive", Integer.toString(timeToLive));
        transaction.expire(sessionKey, timeToLive);

        // Run all commands in the transaction atomically or fail
        final var result = transaction.exec();
        if (result == null || !result.stream().allMatch( o -> o.equals("OK") || o.equals( 1L ) ) ) {
            throw new SessionCreationFailedException();
        }

        return sessionToken;
    }

    @Override
    public void setSessionVariable(String sessionId, String key, String value) throws SessionNotFoundException {
        try( final var jedis = jedisPool.getResource() ) {
            final var sessionKey= SESSION_ID_PREFIX+ sessionId;
            if( !jedis.exists(sessionKey) ) {
                throw new SessionNotFoundException();
            }

            jedis.hset(sessionKey, key, value);
        }
    }

    @Override
    public String getSessionVariable(String sessionId, String key) throws SessionNotFoundException {
        try( final var jedis = jedisPool.getResource() ) {
            final var sessionKey= SESSION_ID_PREFIX+ sessionId;
            if( !jedis.exists(sessionKey) ) {
                throw new SessionNotFoundException();
            }

            return jedis.hget(sessionKey, key);
        }
    }

    @Override
    public Long getUserId(String sessionId) throws SessionNotFoundException {
        return Long.parseLong( getSessionVariable(sessionId, "userId" ) );
    }

    @Override
    public int getTimeToLive(String sessionId) throws SessionNotFoundException {
        return Integer.parseInt( getSessionVariable(sessionId, "timeToLive") );
    }

    @Override
    public String requireSession(Long userId, int timeToLive) throws SessionCreationFailedException {
        try(final var jedis= jedisPool.getResource()) {
            final var userIdKey = USER_ID_PREFIX + userId;
            jedis.watch(userIdKey);

            final var sessionToken= jedis.get( userIdKey );
            if( sessionToken!= null ) {
                jedis.unwatch();
                return sessionToken;
            }

            return createSessionWithJedis( jedis, userId, timeToLive );
        }
    }

    @Override
    public void close() {
        jedisPool.close();
    }
}
