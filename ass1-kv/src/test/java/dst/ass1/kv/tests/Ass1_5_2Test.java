package dst.ass1.kv.tests;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.ISessionManagerFactory;
import dst.ass1.kv.RedisCleaner;
import dst.ass1.kv.impl.SessionManagerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Ass1_5_2Test {

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
    public void testRequireSessionForExistingSession_existingIdReturned() throws Exception {
        String newId = sessionManager.createSession(1337L, 30000);
        assertNotNull(newId);

        String requiredId = sessionManager.requireSession(1337L, 15000);
        assertEquals(newId, requiredId);
    }

    @Test
    public void testRequireSessionForNonExistingSession_newSessionCreated() throws Exception {
        String sessionId = sessionManager.requireSession(1337L, 15000);

        assertEquals(Long.valueOf(1337L), sessionManager.getUserId(sessionId));
    }

}
