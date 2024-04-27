package dst.ass2.service.facade.impl.mapper;

import dst.ass2.service.api.auth.AuthenticationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    @Override
    public Response toResponse(AuthenticationException e) {
        return Response
                .status( Response.Status.FORBIDDEN )
                .entity( e.getMessage() )
                .type( MediaType.TEXT_PLAIN )
                .build();
    }
}
