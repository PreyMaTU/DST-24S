package dst.ass2.service.facade.impl.mapper;

import dst.ass2.service.api.auth.NoSuchUserException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoSuchUserExceptionMapper implements ExceptionMapper<NoSuchUserException> {
    @Override
    public Response toResponse(NoSuchUserException e) {
        return Response
                .status( Response.Status.FORBIDDEN )
                .entity( e.getMessage() )
                .type( MediaType.TEXT_PLAIN )
                .build();
    }
}
