package dst.ass2.service.facade.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.rest.IAuthenticationResource;
import dst.ass2.service.auth.client.IAuthenticationClient;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("auth")
public class AuthenticationResourceFacade implements IAuthenticationResource {
    @Inject
    IAuthenticationClient authenticationClient;

    @Override
    public Response authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        final var token= authenticationClient.authenticate(email, password);
        return Response.ok( token ).build();
    }
}
