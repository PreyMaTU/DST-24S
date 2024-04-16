package dst.ass2.service.auth.client;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;

public interface IAuthenticationClient extends AutoCloseable {

    String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException;

    boolean isTokenValid(String token);

    /**
     * Shuts down any underlying resource required to maintain this client.
     */
    @Override
    void close();
}
