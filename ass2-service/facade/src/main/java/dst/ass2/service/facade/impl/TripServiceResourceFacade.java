package dst.ass2.service.facade.impl;


import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/trips")
public class TripServiceResourceFacade implements ITripServiceResource {
    private final ITripServiceResource tripServiceResource;

    @Inject
    public TripServiceResourceFacade(URI tripServiceURI) {
        /*final var config = new ResourceConfig()
                .packages("dst.ass2")
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
                .register(JacksonFeature.class);

        final var config= new ClientConfig()
                .register(JacksonFeature.class);*/

       final var client = ClientBuilder.newClient();
       final var webTarget = client.target(tripServiceURI);
       tripServiceResource = WebResourceFactory.newResource(ITripServiceResource.class, webTarget);
    }

    @Authenticated
    @Override
    public Response createTrip(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        return tripServiceResource.createTrip(riderId, pickupId, destinationId);
    }

    @Authenticated
    @Override
    public Response confirm(Long tripId) throws EntityNotFoundException, InvalidTripException {
        return tripServiceResource.confirm(tripId);
    }

    @Authenticated
    @Override
    public Response getTrip(Long tripId) throws EntityNotFoundException {
        return tripServiceResource.getTrip(tripId);
    }

    @Authenticated
    @Override
    public Response deleteTrip(Long tripId) throws EntityNotFoundException {
        return tripServiceResource.deleteTrip(tripId);
    }

    @Authenticated
    @Override
    public Response addStop(Long tripId, Long locationId) throws EntityNotFoundException {
        return tripServiceResource.addStop(tripId, locationId);
    }

    @Authenticated
    @Override
    public Response removeStop(Long tripId, Long locationId) throws EntityNotFoundException {
        return tripServiceResource.removeStop(tripId, locationId);
    }

    @Authenticated
    @Override
    public Response match(Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        return tripServiceResource.match(tripId, matchDTO);
    }

    @Authenticated
    @Override
    public Response complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        return tripServiceResource.complete(tripId, tripInfoDTO);
    }

    @Authenticated
    @Override
    public Response cancel(Long tripId) throws EntityNotFoundException {
        return tripServiceResource.cancel(tripId);
    }
}
