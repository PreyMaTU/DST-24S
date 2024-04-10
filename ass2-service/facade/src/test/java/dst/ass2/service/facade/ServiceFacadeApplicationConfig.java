package dst.ass2.service.facade;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.client.AuthenticationClientProperties;
import dst.ass2.service.auth.client.IAuthenticationClient;
import dst.ass2.service.auth.client.impl.GrpcAuthenticationClient;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.net.URI;

@SpringBootConfiguration
public class ServiceFacadeApplicationConfig {

    @Bean
    public ResourceConfig jerseyConfig() {
        return new ResourceConfig()
            .packages("dst.ass2.service.facade");
    }

    @Bean
    public URI tripServiceURI(@Value("${tripservice.uri}") URI target) {
        return target;
    }

    @Bean
    public AuthenticationClientProperties authenticationClientProperties(
        @Value("${auth.host}") String host,
        @Value("${auth.port}") int port) {
        return new AuthenticationClientProperties(host, port);
    }

    @Bean
    @Profile("!AuthenticationResourceTest")
    // only use this when we're not running individual tests
    public IAuthenticationClient grpcAuthenticationClient(AuthenticationClientProperties authenticationClientProperties) {
        return new GrpcAuthenticationClient(authenticationClientProperties);
    }

    @Bean
    @Profile("AuthenticationResourceTest")
    public IAuthenticationClient mockAuthenticationClient() {
        return new MockAuthenticationClient();
    }

    public static class MockAuthenticationClient implements IAuthenticationClient {

        private static final Logger LOG = LoggerFactory.getLogger(MockAuthenticationClient.class);

        public static String TOKEN = "123e4567-e89b-12d3-a456-426655440000";

        @Override
        public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
            LOG.info("Calling MockAuthenticationClient with {}, {}", email, password);

            if (email.equals("junit@example.com")) {
                if (password.equals("junit")) {
                    return TOKEN;
                }
                throw new AuthenticationException();
            }
            throw new NoSuchUserException();
        }

        @Override
        public boolean isTokenValid(String t) {
            return TOKEN.equals(t);
        }

        @Override
        public void close() {
            // pass
        }
    }
}
