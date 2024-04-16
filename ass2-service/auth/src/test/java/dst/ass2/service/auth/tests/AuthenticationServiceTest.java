package dst.ass2.service.auth.tests;

import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.tests.TestData;
import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.AuthenticationServiceApplication;
import dst.ass2.service.auth.ICachingAuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthenticationServiceApplication.class)
@Transactional
@ActiveProfiles("testdata")
public class AuthenticationServiceTest implements ApplicationContextAware {

    @PersistenceContext
    private EntityManager em;

    private ApplicationContext applicationContext;

    private IModelFactory modelFactory;
    private ICachingAuthenticationService authenticationService;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    @Before
    public void setUp() {
        modelFactory = applicationContext.getBean(IModelFactory.class);
        authenticationService = applicationContext.getBean(ICachingAuthenticationService.class);

        // reload the data before each test
        authenticationService.loadData();
    }

    @Test
    public void authenticate_existingUser_createsTokenCorrectly() throws Exception {
        String token = authenticationService.authenticate(TestData.RIDER_1_EMAIL, TestData.RIDER_1_PW);
        assertNotNull(token);
    }

    @Test
    public void authenticate_existingUserNotInCache_createsTokenCorrectly() throws Exception {
        IRider p = modelFactory.createRider();

        p.setEmail("non-cached@example.com");
        try {
            p.setPassword(MessageDigest.getInstance("SHA1").digest("somepw".getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        p.setName("non-cached");
        p.setTel("+43158801");

        em.persist(p);

        String token = authenticationService.authenticate(p.getEmail(), "somepw");
        assertNotNull(token);
    }

    @Test(expected = NoSuchUserException.class)
    public void authenticate_invalidUser_throwsException() throws Exception {
        authenticationService.authenticate("nonexisting@example.com", "foo");
    }

    @Test(expected = AuthenticationException.class)
    public void authenticate_invalidPassword_throwsException() throws Exception {
        authenticationService.authenticate(TestData.RIDER_1_EMAIL, "foo");
    }

    @Test
    public void changePassword_existingUser_passwordChanged() throws Exception {
        authenticationService.changePassword(TestData.RIDER_1_EMAIL, "newPwd");
        assertNotNull(authenticationService.authenticate(TestData.RIDER_1_EMAIL, "newPwd"));
    }

    @Test(expected = NoSuchUserException.class)
    public void changePassword_nonExistingUser_throwsException() throws Exception {
        authenticationService.changePassword("nonexisting@example.com", "foo");
    }

    @Test
    public void getUser_existingToken_returnsUser() throws Exception {
        String token = authenticationService.authenticate(TestData.RIDER_1_EMAIL, TestData.RIDER_1_PW);
        assertEquals(TestData.RIDER_1_EMAIL, authenticationService.getUser(token));
    }

    @Test
    public void getUser_nonExistingToken_returnsNull() throws Exception {
        assertNull(authenticationService.getUser("invalidToken"));
    }

    @Test
    public void isValid_existingToken_returnsTrue() throws Exception {
        String token = authenticationService.authenticate(TestData.RIDER_1_EMAIL, TestData.RIDER_1_PW);
        assertTrue(authenticationService.isValid(token));
    }

    @Test
    public void isValid_nonExistingToken_returnsFalse() throws Exception {
        assertFalse(authenticationService.isValid("invalidToken"));
    }

    @Test
    public void invalidate_validToken_tokenInvalidatedReturnsTrue() throws Exception {
        String token = authenticationService.authenticate(TestData.RIDER_1_EMAIL, TestData.RIDER_1_PW);
        assertTrue(authenticationService.invalidate(token));
        assertFalse(authenticationService.isValid(token));
        assertNull(authenticationService.getUser(token));
    }

    @Test
    public void invalidate_invalidToken_returnsFalse() throws Exception {
        assertFalse(authenticationService.invalidate("invalidToken"));
    }

}
