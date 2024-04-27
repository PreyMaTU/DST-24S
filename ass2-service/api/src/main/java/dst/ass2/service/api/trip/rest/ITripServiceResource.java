package dst.ass2.service.api.trip.rest;

import dst.ass2.service.api.trip.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * This interface exposes the {@code ITripService} as a RESTful interface.
 */
@Path("/trips")
public interface ITripServiceResource {

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    Response createTrip(
            @FormParam("riderId") Long riderId,
            @FormParam("pickupId") Long pickupId,
            @FormParam("destinationId") Long destinationId
    ) throws EntityNotFoundException;

    @PATCH
    @Path("/{tripId}/confirm")
    Response confirm(@PathParam("tripId") Long tripId) throws EntityNotFoundException, InvalidTripException;

    @GET
    @Path("/{tripId}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getTrip(@PathParam("tripId") Long tripId) throws EntityNotFoundException;

    @DELETE
    @Path("/{tripId}")
    Response deleteTrip(@PathParam("tripId") Long tripId) throws EntityNotFoundException;

    @POST
    @Path("/{tripId}/stops")
    @Produces(MediaType.APPLICATION_JSON)
    Response addStop(
            @PathParam("tripId") Long tripId,
            @FormParam("locationId") Long locationId
    ) throws EntityNotFoundException;

    @DELETE
    @Path("/{tripId}/stops/{locationId}")
    Response removeStop(
            @PathParam("tripId") Long tripId,
            @PathParam("locationId") Long locationId
    ) throws EntityNotFoundException;

    @POST()
    @Path("/{tripId}/match")
    @Consumes(MediaType.APPLICATION_JSON)
    Response match(
            @PathParam("tripId") Long tripId,
            MatchDTO matchDTO
    ) throws EntityNotFoundException, DriverNotAvailableException;

    @POST
    @Path("/{tripId}/complete")
    @Consumes(MediaType.APPLICATION_JSON)
    Response complete(
            @PathParam("tripId") Long tripId,
            TripInfoDTO tripInfoDTO
    ) throws EntityNotFoundException;

    @PATCH
    @Path("/{tripId}/cancel")
    Response cancel(@PathParam("tripId") Long tripId) throws EntityNotFoundException;


}
