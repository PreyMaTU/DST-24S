package dst.ass1.jpa.tests;

import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the 1-N association between ITrip and IRider (its creator).
 */
public class Ass1_1_1_08Test extends Ass1_TestBase {

    @Test
    public void testTripRiderAssociation() {

        IRider rider1 = daoFactory.createRiderDAO().findById(testData.rider4Id);
        assertNotNull(rider1);
        assertNotNull(rider1.getTrips());

        List<Long> tripIds = map(rider1.getTrips(), ITrip::getId);

        assertEquals(1, tripIds.size());
        assertThat(tripIds, hasItem(testData.trip6Id));

        ITrip trip1 = daoFactory.createTripDAO().findById(testData.trip6Id);
        ITrip trip2 = daoFactory.createTripDAO().findById(testData.trip1Id);

        assertNotNull(trip1);
        assertNotNull(trip2);

        assertEquals(testData.rider4Id, trip1.getRider().getId());
        assertEquals(testData.rider1Id, trip2.getRider().getId());
    }

    @Test
    public void testTripStatus() {
        ITrip trip1 = daoFactory.createTripDAO().findById(testData.trip1Id);
        assertEquals(TripState.COMPLETED, trip1.getState());
        ITrip trip2 = daoFactory.createTripDAO().findById(testData.trip6Id);
        assertEquals(TripState.CREATED, trip2.getState());
    }

}
