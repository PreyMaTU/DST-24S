package dst.ass2.service.facade.impl;

import dst.ass2.service.auth.client.IAuthenticationClient;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Authenticated
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    IAuthenticationClient authenticationClient;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        final var header = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        if( header == null ) {
            throw new NotAuthorizedException("Missing authorization header");
        }

        if( !header.startsWith(BEARER_PREFIX) ) {
            throw new NotAuthorizedException("Invalid authorization header");
        }

        final var token= header.substring(BEARER_PREFIX.length());
        if( !authenticationClient.isTokenValid(token) ) {
            throw new NotAuthorizedException("Invalid authorization token");
        }
    }
}
