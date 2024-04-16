package dst.ass2.service.auth.client;

import dst.ass1.jpa.tests.TestData;
import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.AuthenticationServiceApplication;
import dst.ass2.service.auth.client.impl.GrpcAuthenticationClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthenticationServiceApplication.class)
@Transactional
@ActiveProfiles({"testdata", "grpc"})
public class AuthenticationClientTest {

    @Value("${grpc.port}")
    private int port;

    private IAuthenticationClient client;

    @Before
    public void setUp() throws Exception {
        AuthenticationClientProperties properties = new AuthenticationClientProperties("localhost", port);
        client = new GrpcAuthenticationClient(properties);
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test(expected = NoSuchUserException.class)
    public void authenticate_invalidUser_throwsException() throws Exception {
        client.authenticate("nonexisting@example.com", "foo");
    }

    @Test(expected = AuthenticationException.class)
    public void authenticate_invalidPassword_throwsException() throws Exception {
        client.authenticate(TestData.RIDER_1_EMAIL, "foo");
    }

    @Test
    public void authenticate_existingUser_returnsToken() throws Exception {
        String token = client.authenticate(TestData.RIDER_1_EMAIL, TestData.RIDER_1_PW);
        assertNotNull(token);
    }

    @Test
    public void isTokenValid_invalidToken_returnsFalse() throws Exception {
        boolean valid = client.isTokenValid(UUID.randomUUID().toString()); // should be false in *almost* all cases ;-)
        assertFalse(valid);
    }

    @Test
    public void isTokenValid_onCreatedToken_returnsTrue() throws Exception {
        String token = client.authenticate(TestData.RIDER_2_EMAIL, TestData.RIDER_2_PW);
        boolean valid = client.isTokenValid(token);
        assertTrue(valid);
    }

}
