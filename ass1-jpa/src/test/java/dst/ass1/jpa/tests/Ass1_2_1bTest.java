package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ass1_2_1bTest extends Ass1_TestBase {

    private ITripDAO tripDAO;

    @Before
    public void setUp() throws Exception {
        tripDAO = daoFactory.createTripDAO();
    }

    @Test
    public void findTripsByState_forOneApproachingTrip_returnsOne() throws Exception {
        List<ITrip> trips = tripDAO.findByStatus(TripState.APPROACHING);
        assertEquals(1, trips.size());
        assertEquals(trips.get(0).getId(), this.testData.trip7Id);
    }

    @Test
    public void findTripsByState_forFiveCompletedTrips_returnsSix() throws Exception {
        List<ITrip> trips = tripDAO.findByStatus(TripState.COMPLETED);
        assertEquals(6, trips.size());
    }


}
