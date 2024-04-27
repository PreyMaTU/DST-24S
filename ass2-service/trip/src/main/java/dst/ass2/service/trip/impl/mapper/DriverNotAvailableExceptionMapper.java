package dst.ass2.service.trip.impl.mapper;

import dst.ass2.service.api.trip.DriverNotAvailableException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DriverNotAvailableExceptionMapper implements ExceptionMapper<DriverNotAvailableException> {

    @Override
    public Response toResponse(DriverNotAvailableException e) {
        // Driver is already occupied with another trip -> CONFLICT
        return Response
                .status( Response.Status.CONFLICT )
                .entity( e.getMessage() )
                .type( MediaType.TEXT_PLAIN )
                .build();
    }
}
