package dst.ass1.kv.tests;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.ISessionManagerFactory;
import dst.ass1.kv.RedisCleaner;
import dst.ass1.kv.SessionNotFoundException;
import dst.ass1.kv.impl.SessionManagerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class Ass1_5_1Test {

    @Rule
    public RedisCleaner redisRule = new RedisCleaner();

    private ISessionManager sessionManager;

    @Before
    public void setUp() {
        ISessionManagerFactory sessionManagerFactory = new SessionManagerFactory();

        sessionManager = sessionManagerFactory.createSessionManager(redisRule.getProperties());
    }

    @After
    public void tearDown() {
        sessionManager.close();
    }

    @Test
    public void createSession_createsSessionId() throws Exception {
        String sessionId = sessionManager.createSession(1337L, 15000);
        assertNotNull(sessionId);
    }

    @Test
    public void getAndSetSessionVariable_behavesCorrectly() throws Exception {
        String sessionId = sessionManager.createSession(1337L, 15000);
        sessionManager.setSessionVariable(sessionId, "f00", "bar");
        assertEquals("bar", sessionManager.getSessionVariable(sessionId, "f00"));
    }

    @Test(expected = SessionNotFoundException.class)
    public void setSessionVariable_forNonExistingSession_throwsSessionNotFoundException() throws Exception {
        sessionManager.setSessionVariable("nonExistingSessionId", "f00", "bar");
    }

    @Test(expected = SessionNotFoundException.class)
    public void getSessionVariable_forNonExistingSession_throwsSessionNotFoundException() throws Exception {
        sessionManager.getSessionVariable("nonExistingSessionId", "f00");
    }

    @Test
    public void getSessionVariable_onNonExistingVariable_returnsNull() throws Exception {
        String sessionId = sessionManager.createSession(1337L, 15000);
        String value = sessionManager.getSessionVariable(sessionId, "f00");
        assertNull(value);
    }

    @Test
    public void getUserId_returnsCorrectUserID() throws Exception {
        Long userId = 1337L;
        String sessionId = sessionManager.createSession(userId, 15000);
        assertEquals(userId, sessionManager.getUserId(sessionId));
    }

    @Test(expected = SessionNotFoundException.class)
    public void getUserId_forNonExistingSession_throwsException() throws Exception {
        sessionManager.getUserId("nonExistingSessionId");
    }

    @Test
    public void getTimeToLive_returnsCorrectValue() throws Exception {
        String sessionId = sessionManager.createSession(1337L, 60);
        int ttl = sessionManager.getTimeToLive(sessionId);
        assertThat(ttl, allOf(greaterThan(57), lessThan(61)));
    }

    @Test
    public void getTimeToLive_afterExpiry_throwsSessionNotFoundException() throws Exception {
        String sessionId = sessionManager.createSession(1337L, 2);
        int ttl = sessionManager.getTimeToLive(sessionId);
        assertThat(ttl, greaterThan(0));
        Thread.sleep(3000);

        assertThrows(SessionNotFoundException.class, () -> {
            sessionManager.getTimeToLive(sessionId);
        });
    }

    @Test(expected = SessionNotFoundException.class)
    public void getTimeToLive_forNonExistingSession_throwsSessionNotFoundException() throws Exception {
        sessionManager.getTimeToLive("nonExistingSessionId");
    }

}
