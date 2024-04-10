package dst.ass2.service.api.trip.rest;

import dst.ass2.service.api.trip.*;

import javax.ws.rs.core.Response;


/**
 * This interface exposes the {@code ITripService} as a RESTful interface.
 */
public interface ITripServiceResource {

    // TODO annotate the class and methods with the correct javax.ws.rs annotations

    Response createTrip(Long riderId, Long pickupId, Long destinationId)
        throws EntityNotFoundException;

    Response confirm(Long tripId) throws EntityNotFoundException, InvalidTripException;

    Response getTrip(Long tripId) throws EntityNotFoundException;

    Response deleteTrip(Long tripId) throws EntityNotFoundException;

    Response addStop(Long tripId, Long locationId) throws EntityNotFoundException;

    Response removeStop(Long tripId, Long locationId) throws EntityNotFoundException;

    Response match(Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException;

    Response complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException;

    Response cancel(Long tripId) throws EntityNotFoundException;


}
