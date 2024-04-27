package dst.ass2.service.trip.impl.mapper;

import dst.ass2.service.api.trip.InvalidTripException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidTripExceptionMapper implements ExceptionMapper<InvalidTripException> {

    @Override
    public Response toResponse(InvalidTripException e) {
        return Response
                .status( 422 ) // Unprocessable entity
                .entity( "Invalid trip entity. Fare could not be calculated." )
                .type( MediaType.TEXT_PLAIN )
                .build();
    }
}
