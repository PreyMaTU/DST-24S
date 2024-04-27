package dst.ass2.service.trip.impl.rest;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

// Class/Interface level annotations do not work when implementing an interface,
// hence we add it here again
@Path("/trips")
public class TripServiceResource implements ITripServiceResource {
    @Inject
    private ITripService tripService;

    @Override
    public Response createTrip(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        final var trip= tripService.create( riderId, pickupId, destinationId );
        return Response
                .status( Response.Status.CREATED )
                .entity( trip.getId() )
                .build();
    }

    @Override
    public Response confirm(Long tripId) throws EntityNotFoundException, InvalidTripException {
        tripService.confirm( tripId );
        return Response.ok().build();
    }

    @Override
    public Response getTrip(Long tripId) throws EntityNotFoundException {
        final var trip= tripService.find( tripId );
        if( trip == null ) {
            throw new EntityNotFoundException( String.format("Trip with id %d does not exist", tripId) );
        }

        return Response.ok( trip ).build();
    }

    @Override
    public Response deleteTrip(Long tripId) throws EntityNotFoundException {
        tripService.delete( tripId );
        return Response.ok().build();
    }

    @Override
    public Response addStop(Long tripId, Long locationId) throws EntityNotFoundException {
        final var trip = tripService.find( tripId );
        if( trip == null ) {
            throw new EntityNotFoundException( String.format("Trip with id %d does not exist", tripId) );
        }

        if( !tripService.addStop( trip, locationId ) ) {
            return Response.status( Response.Status.CONFLICT ).build();
        }

        return Response.ok( trip.getFare() ).build();
    }

    @Override
    public Response removeStop(Long tripId, Long locationId) throws EntityNotFoundException {
        final var trip = tripService.find( tripId );
        if( trip == null ) {
            throw new EntityNotFoundException( String.format("Trip with id %d does not exist", tripId) );
        }

        if( !tripService.removeStop( trip, locationId ) ) {
            return Response.status( Response.Status.CONFLICT ).build();
        }

        return Response.ok().build();
    }

    @Override
    public Response match(Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        tripService.match( tripId, matchDTO );
        return Response.status( Response.Status.CREATED ).build();
    }

    @Override
    public Response complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        tripService.complete( tripId, tripInfoDTO );
        return Response.ok().build();
    }

    @Override
    public Response cancel(Long tripId) throws EntityNotFoundException {
        tripService.cancel( tripId );
        return Response.ok().build();
    }
}
