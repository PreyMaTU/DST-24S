package dst.ass2.service.trip.tests;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.*;
import dst.ass1.jpa.tests.TestData;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;
import dst.ass2.service.trip.TripApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TripApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testdata")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TripServiceTest implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(TripServiceTest.class);

    private ApplicationContext ctx;

    @MockBean
    private IMatchingService matchingService;

    private ITripService tripService;
    private IDAOFactory daoFactory;
    private TestData testData;
    private ITripDAO tripDAO;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    @Before
    public void setUp() {
        LOG.info("Test resolving beans from application context");
        daoFactory = ctx.getBean(IDAOFactory.class);
        tripService = ctx.getBean(ITripService.class);
        testData = ctx.getBean(TestData.class);
        tripDAO = daoFactory.createTripDAO();
    }

    @Test
    public void testCreateWithValidArguments_persistsTrip_and_returnsTripDTO() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getOne());

        TripDTO tripDTO = tripService.create(testData.rider1Id, testData.location1Id, testData.location2Id);

        verify(matchingService, times(1)).calculateFare(any());

        assertNotNull(tripDTO);
        assertNotNull(tripDTO.getId());
        assertNotNull(tripDTO.getFare());

        assertEquals(testData.rider1Id, tripDTO.getRiderId());
        assertEquals(testData.location1Id, tripDTO.getPickupId());
        assertEquals(testData.location2Id, tripDTO.getDestinationId());
        assertEquals(getOne(), tripDTO.getFare());

        ITrip trip = tripDAO.findById(tripDTO.getId());
        assertNotNull(trip);

        assertEquals(testData.location1Id, trip.getPickup().getId());
        assertEquals(testData.location2Id, trip.getDestination().getId());
        assertEquals(testData.rider1Id, trip.getRider().getId());
        assertEquals(TripState.CREATED, trip.getState());
    }


    @Test(expected = EntityNotFoundException.class)
    public void testCreateWithInvalidRider_throwsException() throws Exception {
        tripService.create(1337L, testData.location1Id, testData.location2Id);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCreateWithInvalidPickup_throwsException() throws Exception {
        tripService.create(testData.rider1Id, 1444L, testData.location2Id);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCreateWithInvalidDestination_throwsException() throws Exception {
        tripService.create(testData.rider1Id, testData.location1Id, 1337L);
    }

    @Test
    public void testCreateWithInvalidTrip_setsFareToNull() throws Exception {
        when(matchingService.calculateFare(any())).thenThrow(new InvalidTripException());

        TripDTO tripDTO = tripService.create(testData.rider1Id, testData.location1Id, testData.location2Id);
        assertNotNull(tripDTO);
        assertNotNull(tripDTO.getId());

        ITrip trip = tripDAO.findById(tripDTO.getId());
        assertNotNull(trip);
        assertNull(tripDTO.getFare());
    }

    @Test
    public void testConfirmWithValidTrip_isQueued() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        tripService.confirm(testData.trip6Id);
        ITrip confirmed = tripDAO.findById(testData.trip6Id);
        assertEquals(TripState.QUEUED, confirmed.getState());
        verify(matchingService, times(1)).queueTripForMatching(any());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testConfirmWithUnknownTrip_throwsException() throws Exception {
        tripService.confirm(1337L);
    }

    @Test(expected = IllegalStateException.class)
    public void testConfirmQueuedTrip_throwsException() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getOne());
        tripService.confirm(testData.trip10Id);
    }

    @Test(expected = InvalidTripException.class)
    public void testConfirmWithInvalidTrip_throwsException() throws Exception {
        when(matchingService.calculateFare(any())).thenThrow(new InvalidTripException());
        tripService.confirm(testData.trip6Id);
    }

    @Test
    public void testMatch_matchCreated_stateMatched() throws Exception {
        Long tripId = testData.trip10Id;

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setFare(getOne());
        matchDTO.setDriverId(testData.driver4Id + 1);
        matchDTO.setVehicleId(testData.vehicle1Id);

        tripService.match(tripId, matchDTO);

        ITrip updated = tripDAO.findById(tripId);
        assertNotNull(updated);
        assertEquals(TripState.MATCHED, updated.getState());

        IMatch match = updated.getMatch();
        assertNotNull(match);
        assertEquals(matchDTO.getDriverId(), match.getDriver().getId());
        assertEquals(matchDTO.getVehicleId(), match.getVehicle().getId());

        IMoney fare = match.getFare();
        assertEquals(getOne().getCurrency(), fare.getCurrency());
        assertEquals(getOne().getValue().compareTo(fare.getCurrencyValue()), 0);
    }

    @Test
    public void testMatchWithUnknownTrip_throwsException_and_requeueTrip() throws Exception {
        long tripId = 1337L;

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setFare(getOne());
        matchDTO.setDriverId(testData.driver4Id + 1);
        matchDTO.setVehicleId(testData.vehicle1Id);

        try {
            tripService.match(tripId, matchDTO);
        } catch (EntityNotFoundException ex) {
            verify(matchingService, times(1)).queueTripForMatching(any());
            return;
        }

        fail();
    }

    @Test
    public void testMatch_driverAlreadyAssigned_throwsException_and_requeueTrip() throws Exception {
        Long tripId = testData.trip10Id;

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setFare(getTen());
        matchDTO.setDriverId(testData.driver4Id);
        matchDTO.setVehicleId(testData.vehicle1Id);

        try {
            tripService.match(tripId, matchDTO);
        } catch (DriverNotAvailableException ex) {
            verify(matchingService, times(1)).queueTripForMatching(any());
            return;
        }

        fail();
    }

    @Test
    public void testCompleteWithValidTripInfo_shouldPersistTripInfo_and_setTripCompleted() throws Exception {
        Long tripId = testData.trip9Id;
        TripInfoDTO tripInfoDTO = new TripInfoDTO();
        tripInfoDTO.setCompleted(new Date());
        tripInfoDTO.setFare(getTen());
        tripInfoDTO.setDistance(2.0);

        tripService.complete(tripId, tripInfoDTO);

        ITrip updated = tripDAO.findById(tripId);
        assertNotNull(updated);
        assertEquals(TripState.COMPLETED, updated.getState());

        ITripInfo tripInfo = updated.getTripInfo();
        assertNotNull(tripInfo);

        assertEquals(tripInfoDTO.getCompleted(), tripInfo.getCompleted());
        assertEquals(2.0, tripInfo.getDistance(), 0);
        assertEquals(getTen().getCurrency(), tripInfo.getReceipt().getTotal().getCurrency());
        assertEquals(0, getTen().getValue().compareTo(tripInfo.getReceipt().getTotal().getCurrencyValue()));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCompleteWithUnknownTrip_throwsException() throws Exception {
        long tripId = 1337L;
        TripInfoDTO tripInfoDTO = new TripInfoDTO();
        tripInfoDTO.setCompleted(new Date());
        tripInfoDTO.setFare(getTen());
        tripInfoDTO.setDistance(2.0);

        tripService.complete(tripId, tripInfoDTO);
    }

    @Test
    public void testCancelWithValidTrip_shouldCancelTrip() throws Exception {
        tripService.cancel(testData.trip6Id);

        ITrip updated = tripDAO.findById(testData.trip6Id);
        assertNotNull(updated);
        assertEquals(TripState.CANCELLED, updated.getState());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCancelWithUnkownTrip_throwsException() throws Exception {
        tripService.cancel(1337L);
    }

    @Test
    public void testAddStop_shouldReturnTrue() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        TripDTO tripDTO = getTrip6DTO();
        tripDTO.setFare(getTen());

        boolean added = tripService.addStop(tripDTO, testData.location1Id);
        assertTrue(added);
        assertEquals(getTen(), tripDTO.getFare());
        verify(matchingService, times(1)).calculateFare(any());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testAddStopUnknownLocation_throwsException() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        TripDTO tripDTO = getTrip6DTO();
        tripDTO.setFare(getTen());

        tripService.addStop(tripDTO, 1344L);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddStopInvalidTripState_throwsException() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        ITrip trip = tripDAO.findById(testData.trip1Id);
        TripDTO tripDTO = new TripDTO();
        tripDTO.setId(trip.getId());
        tripDTO.setPickupId(trip.getPickup().getId());
        tripDTO.setDestinationId(trip.getDestination().getId());
        tripDTO.setRiderId(trip.getRider().getId());
        tripDTO.setStops(trip.getStops().stream().map(ILocation::getId).collect(Collectors.toList()));
        tripDTO.setFare(getTen());

        tripService.addStop(tripDTO, testData.location2Id);
    }

    @Test
    public void testAddStopInvalidTrip_setsFareToNull() throws Exception {
        when(matchingService.calculateFare(any())).thenThrow(new InvalidTripException());

        TripDTO tripDTO = getTrip6DTO();
        tripDTO.setFare(getOne());

        boolean added = tripService.addStop(tripDTO, testData.location1Id);
        assertTrue(added);
        assertNull(tripDTO.getFare());
        assertEquals(4, tripDTO.getStops().size());
    }


    @Test
    public void testAddStop_shouldReturnFalse() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        TripDTO tripDTO = getTrip6DTO();
        tripDTO.setFare(getOne());

        boolean added = tripService.addStop(tripDTO, testData.location2Id);
        assertFalse(added);

        //fare should not be updated if the location hasn't been added
        assertEquals(getOne(), tripDTO.getFare());
    }

    @Test
    public void testRemoveStop_shouldReturnTrue() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getOne());

        TripDTO tripDTO = getTrip6DTO();
        tripDTO.setFare(getTen());

        boolean removed = tripService.removeStop(tripDTO, testData.location2Id);
        assertTrue(removed);
        assertEquals(getOne(), tripDTO.getFare());
        assertEquals(2, tripDTO.getStops().size());
        ITrip updated = tripDAO.findById(testData.trip6Id);
        assertEquals(2, updated.getStops().size());
        verify(matchingService, times(1)).calculateFare(any());
    }

    private MoneyDTO getZero() {
        MoneyDTO moneyDTO = new MoneyDTO();
        moneyDTO.setCurrency("EUR");
        moneyDTO.setValue(BigDecimal.ZERO);
        return moneyDTO;
    }

    @Test
    public void testRemoveStop_shouldReturnFalse() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getZero());
        TripDTO tripDTO = getTrip6DTO();
        tripDTO.setFare(getOne());

        boolean removed = tripService.removeStop(tripDTO, testData.location1Id);
        assertFalse(removed);

        //fare should not be updated if the location hasn't been removed
        assertEquals(getOne(), tripDTO.getFare());

        assertEquals(3, tripDTO.getStops().size());
        ITrip updated = tripDAO.findById(testData.trip6Id);
        assertEquals(3, updated.getStops().size());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testRemoveStopUnknownLocation_throwsException() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getOne());

        TripDTO tripDTO = getTrip6DTO();
        tripDTO.setFare(getTen());

        tripService.removeStop(tripDTO, 1344L);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveStopInvalidTripState_throwsException() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getOne());

        ITrip trip = tripDAO.findById(testData.trip10Id);
        TripDTO tripDTO = new TripDTO();
        tripDTO.setId(trip.getId());
        tripDTO.setPickupId(trip.getPickup().getId());
        tripDTO.setDestinationId(trip.getDestination().getId());
        tripDTO.setRiderId(trip.getRider().getId());
        List<Long> stopIds = trip.getStops().stream().map(ILocation::getId).collect(Collectors.toList());
        tripDTO.setStops(stopIds);
        tripDTO.setFare(getTen());

        tripService.removeStop(tripDTO, testData.location4Id);
    }

    @Test
    public void testRemoveStopInvalidTrip_setsFareToNull() throws Exception {
        when(matchingService.calculateFare(any())).thenThrow(new InvalidTripException());

        TripDTO tripDTO = getTrip6DTO();
        tripDTO.setFare(getOne());

        boolean removed = tripService.removeStop(tripDTO, testData.location4Id);
        assertTrue(removed);
        assertNull(tripDTO.getFare());
    }

    @Test
    public void testDeleteValidTrip_shouldSucceed() throws Exception {
        tripService.delete(testData.trip6Id);
        ITrip deleted = tripDAO.findById(testData.trip6Id);
        assertNull(deleted);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteUnknownTrip_throwsException() throws Exception {
        tripService.delete(1111L);
    }

    @Test
    public void testFindTrip_shouldSucceed() throws Exception {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        TripDTO tripDTO = tripService.find(testData.trip9Id);
        assertNotNull(tripDTO);
        assertEquals(testData.trip9Id, tripDTO.getId());
        assertEquals(testData.rider1Id, tripDTO.getRiderId());
        assertEquals(testData.location2Id, tripDTO.getPickupId());
        assertEquals(testData.location5Id, tripDTO.getDestinationId());
        assertEquals(getTen(), tripDTO.getFare());
    }

    @Test
    public void testFindTrip_shouldReturnNull() {
        TripDTO tripDTO = tripService.find(1337L);
        assertNull(tripDTO);
    }

    private MoneyDTO getOne() {
        MoneyDTO moneyDTO = new MoneyDTO();
        moneyDTO.setCurrency("EUR");
        moneyDTO.setValue(BigDecimal.ONE);
        return moneyDTO;
    }

    private MoneyDTO getTen() {
        MoneyDTO moneyDTO = new MoneyDTO();
        moneyDTO.setValue(BigDecimal.TEN);
        moneyDTO.setCurrency("EUR");
        return moneyDTO;
    }


    private TripDTO getTrip6DTO() {
        TripDTO tripDTO = new TripDTO();
        tripDTO.setId(testData.trip6Id);
        tripDTO.setPickupId(testData.location5Id);
        tripDTO.setDestinationId(testData.location1Id);
        tripDTO.setRiderId(testData.rider4Id);
        LinkedList<Long> longs = new LinkedList<>();
        longs.add(testData.location2Id);
        longs.add(testData.location3Id);
        longs.add(testData.location4Id);
        tripDTO.setStops(longs);
        return tripDTO;
    }
}
