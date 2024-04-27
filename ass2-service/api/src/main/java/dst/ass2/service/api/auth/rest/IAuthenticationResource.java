package dst.ass2.service.api.auth.rest;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The IAuthenticationResource exposes parts of the {@code IAuthenticationService} as a RESTful interface.
 */
@Path("/auth")
public interface IAuthenticationResource {

    @POST
    @Path("/authenticate")
    @Produces( MediaType.TEXT_PLAIN )
    Response authenticate(
            @FormParam("email") String email,
            @FormParam("password") String password
    ) throws NoSuchUserException, AuthenticationException;

}
