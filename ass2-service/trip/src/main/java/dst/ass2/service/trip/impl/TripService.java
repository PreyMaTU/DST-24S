package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.GenericDAO;
import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.TripState;
import dst.ass1.jpa.model.ITrip;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.stream.Collectors;

@Singleton
@ManagedBean
public class TripService implements ITripService {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    IDAOFactory daoFactory;

    @Inject
    IModelFactory modelFactory;

    @Inject
    IMatchingService matchingService;

    private TripDTO makeTripDTOWithFare( ITrip trip ) {
        final var dto= TripDTO.fromTrip( trip );
        if( dto == null ) {
            return null;
        }

        try {
            dto.setFare( matchingService.calculateFare(dto) );
        } catch( InvalidTripException e ) {
            dto.setFare(null);
        }

        return dto;
    }

    private <T> T getById(GenericDAO<T> dao, Long id) throws EntityNotFoundException {
        final var entity= dao.findById( id );
        if( entity == null ) {
            throw new EntityNotFoundException( String.format("%s with ID %d does not exist", dao.getEntityName(), id) );
        }

        return entity;
    }

    @Override
    @Transactional
    public TripDTO create(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        final var riders = daoFactory.createRiderDAO();
        final var locations = daoFactory.createLocationDAO();

        final var trip = modelFactory.createTrip();
        final var rider= getById(riders, riderId);

        final var pickupLocation = getById(locations, pickupId);
        final var destinationLocation = getById(locations, destinationId);

        trip.setState( TripState.CREATED );
        trip.setRider( rider );
        trip.setPickup( pickupLocation );
        trip.setDestination( destinationLocation );
        entityManager.persist( trip );

        return makeTripDTOWithFare( trip );
    }

    @Override
    @Transactional
    public void confirm(Long tripId) throws EntityNotFoundException, IllegalStateException, InvalidTripException {
        final var trips= daoFactory.createTripDAO();
        final var trip= getById(trips, tripId);

        if( trip.getState() != TripState.CREATED ) {
            throw new IllegalStateException();
        }

        // Just check if the trip is valid. The fare value is unused
        matchingService.calculateFare( TripDTO.fromTrip( trip ) );

        trip.setState( TripState.QUEUED );
        entityManager.persist( trip );

        matchingService.queueTripForMatching(tripId);
    }

    @Override
    @Transactional
    public void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException {
        try {
            final var trips = daoFactory.createTripDAO();
            final var trip= getById(trips, tripId);

            if( trip.getState() != TripState.QUEUED || trip.getRider() == null ) {
                throw new IllegalStateException( "Trip state is not QUEUED" );
            }

            final var drivers= daoFactory.createDriverDAO();
            final var driver= getById(drivers, match.getDriverId());

            final var vehicles= daoFactory.createVehicleDAO();
            final var vehicle= getById( vehicles, match.getVehicleId());

            if( !trips.findInProgressTripsByDriver( driver.getId() ).isEmpty() ) {
                throw new DriverNotAvailableException( String.format("Driver with ID %d is already assigned", driver.getId()) );
            }

            final var money= modelFactory.createMoney();
            money.setCurrency( match.getFare().getCurrency() );
            money.setCurrencyValue( match.getFare().getValue() );

            final var matchModel= modelFactory.createMatch();
            matchModel.setTrip( trip );
            matchModel.setDriver( driver );
            matchModel.setVehicle( vehicle );
            matchModel.setFare( money );
            matchModel.setDate( new Date() );
            entityManager.persist( matchModel );

            trip.setState( TripState.MATCHED );
            entityManager.persist( trip );

        } catch( Exception e ) {
            matchingService.queueTripForMatching( tripId );
            throw e;
        }
    }

    @Override
    @Transactional
    public void complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        final var trips = daoFactory.createTripDAO();
        final var trip= getById(trips, tripId);

        trip.setState(TripState.COMPLETED);

        final var paymentInfos= daoFactory.createPaymentInfoDAO();
        final var paymentInfo= paymentInfos.findPreferredOrAnyPaymentInfoByRider( trip.getRider().getId() );
        if( paymentInfo == null ) {
            throw new EntityNotFoundException( String.format("Rider with ID %d does not have a payment info", trip.getRider().getId()) );
        }

        final var total = modelFactory.createMoney();
        total.setCurrency(tripInfoDTO.getFare().getCurrency());
        total.setCurrencyValue(tripInfoDTO.getFare().getValue());

        final var receipt= modelFactory.createTripReceipt();
        receipt.setTotal( total );
        receipt.setPaymentInfo( paymentInfo );

        final var tripInfo= modelFactory.createTripInfo();
        tripInfo.setTrip( trip );
        tripInfo.setCompleted( tripInfoDTO.getCompleted() );
        tripInfo.setDistance( tripInfoDTO.getDistance() );
        tripInfo.setReceipt( receipt );

        receipt.setTripInfo( tripInfo );
        trip.setTripInfo( tripInfo );

        entityManager.persist(tripInfo);
    }

    @Override
    @Transactional
    public void cancel(Long tripId) throws EntityNotFoundException {
        final var trips = daoFactory.createTripDAO();
        final var trip= getById(trips, tripId);

        trip.setState( TripState.CANCELLED );
        entityManager.persist( trip );
    }

    @Override
    @Transactional
    public boolean addStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        final var trips= daoFactory.createTripDAO();
        final var tripModel= getById(trips, trip.getId() );

        if(tripModel.getState() != TripState.CREATED){
            throw new IllegalStateException( "Trip state is not CREATED" );
        }

        final var locations= daoFactory.createLocationDAO();
        final var location= getById( locations, locationId );

        if ( trip.getStops().contains(locationId) ) {
            return false;
        }

        tripModel.addStop(location);
        entityManager.persist( tripModel );

        trip.getStops().add( locationId );

        try {
            trip.setFare( matchingService.calculateFare(trip) );
        } catch( InvalidTripException e ) {
            trip.setFare( null );
        }

        return true;
    }

    @Override
    @Transactional
    public boolean removeStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        final var trips= daoFactory.createTripDAO();
        final var tripModel= getById(trips, trip.getId() );

        if(tripModel.getState() != TripState.CREATED){
            throw new IllegalStateException( "Trip state is not CREATED" );
        }

        final var locations= daoFactory.createLocationDAO();
        final var location= getById( locations, locationId );

        // Filter out the location id from the original stops
        final var originalStops= tripModel.getStops();
        final var filteredStops= originalStops.stream()
                .filter( stop -> !stop.getId().equals(locationId) )
                .collect(Collectors.toList());

        // Return if we did not remove anything
        if( originalStops.size() == filteredStops.size() ) {
            return false;
        }

        tripModel.setStops( filteredStops );
        entityManager.persist( tripModel );

        trip.getStops().remove( locationId );

        try {
            trip.setFare( matchingService.calculateFare(trip) );
        } catch( InvalidTripException e ) {
            trip.setFare( null );
        }

        return true;
    }

    @Override
    @Transactional
    public void delete(Long tripId) throws EntityNotFoundException {
        final var trips = daoFactory.createTripDAO();
        final var trip= getById(trips, tripId);

        entityManager.remove( trip );
    }

    @Override
    public TripDTO find(Long tripId) {
        final var trips = daoFactory.createTripDAO();
        return makeTripDTOWithFare( trips.findById( tripId ) );
    }
}
