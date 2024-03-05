package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.ITripInfoDAO;
import dst.ass1.jpa.util.TupleResult;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ass1_2_1cTest extends Ass1_TestBase {

    private ITripInfoDAO tripInfoDao;

    @Before
    public void setUp() throws Exception {
        tripInfoDao = daoFactory.createTripInfoDAO();
    }

    @Test
    public void testFindAverageRatingByDriver() {
        List<TupleResult<Long, Double>> result = tripInfoDao.findRidersAverageRating();
        assertEquals(3, result.size());
        assertEquals(testData.rider2Id.longValue(), result.get(0).getFirst().longValue());
        assertEquals(5.5, result.get(0).getSecond(), 0.001);
        assertEquals(testData.rider3Id.longValue(), result.get(1).getFirst().longValue());
        assertEquals(2.0, result.get(1).getSecond(), 0.001);
        assertEquals(testData.rider1Id.longValue(), result.get(2).getFirst().longValue());
        assertEquals(1.5, result.get(2).getSecond(), 0.001);
    }

}
