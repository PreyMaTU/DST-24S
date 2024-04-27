package dst.ass2.service.trip.impl.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalStateExceptionMapper implements ExceptionMapper<IllegalStateException> {
    @Override
    public Response toResponse(IllegalStateException e) {
        return Response
                .status( Response.Status.BAD_REQUEST )
                .entity( e.getMessage() )
                .type( MediaType.TEXT_PLAIN )
                .build();
    }
}
