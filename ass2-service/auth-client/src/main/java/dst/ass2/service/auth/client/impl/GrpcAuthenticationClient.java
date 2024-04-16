package dst.ass2.service.auth.client.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.client.AuthenticationClientProperties;
import dst.ass2.service.auth.client.IAuthenticationClient;

public class GrpcAuthenticationClient implements IAuthenticationClient {

    // TODO make use of the generated grpc sources to implement a blocking client

    public GrpcAuthenticationClient(AuthenticationClientProperties properties) {
        // TODO
    }

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        // TODO
        return null;
    }

    @Override
    public boolean isTokenValid(String token) {
        // TODO
        return false;
    }

    @Override
    public void close() {
        // TODO
    }
}
