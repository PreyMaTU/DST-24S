package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static dst.ass1.jpa.tests.TestData.localDateToDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class Ass1_2_2Test extends Ass1_TestBase {

    private IRiderDAO riderDAO;

    @Before
    public void setup() {
        riderDAO = daoFactory.createRiderDAO();
    }

    @Test
    public void findInactiveRiders_nullArgument_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> riderDAO.findRidersWithNoTrips(null, Date.from(Instant.now())));
    }

    @Test
    public void findInactiveRiders_returnsEmptyList() {
        Date start = Date.from(Instant.ofEpochSecond(788914800));
        Date end = Date.from(Instant.ofEpochSecond(789001200));
        List<IRider> riders = riderDAO.findRidersWithNoTrips(start, end);
        assertEquals(4, riders.size());
    }

    @Test
    public void findInactiveRiders_returnsTwoRiders() {
        LocalDate now = LocalDate.now();
        var start = localDateToDate(now.minusDays(11));
        var end = localDateToDate(now);
        List<IRider> riders = riderDAO.findRidersWithNoTrips(start, end);
        assertEquals(2, riders.size());
    }

    @Test
    public void findInactiveRiders_returnsThreeRiders() {
        LocalDate now = LocalDate.now();
        var start = localDateToDate(now.minusDays(40));
        var end = localDateToDate(now);
        List<IRider> riders = riderDAO.findRidersWithNoTrips(start, end);
        assertEquals(1, riders.size());
    }

}
