package dst.ass1.doc.tests;

import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.MongoService;
import dst.ass1.jpa.util.Constants;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.StreamSupport;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Ass1_4_3_01Test {

    @ClassRule
    public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();

    @Rule
    public MongoService mongo = new MongoService(db -> {
        boolean exists = StreamSupport.stream(db.listCollectionNames().spliterator(), false)
            .anyMatch(Constants.COLL_LOCATION_DATA::equalsIgnoreCase);

        if (!exists) {
            db.createCollection(Constants.COLL_LOCATION_DATA); // make sure the empty collection exists
        }
    });

    @Test
    public void getAvgClosingTimes_withEmptyData_returnsEmptyResult() throws Exception {
        var documentStatistics = mongo.getDocumentQuery().getAverageOpeningHoursPerCategory();
        assertNotNull(documentStatistics);
        assertTrue(documentStatistics.isEmpty());
    }
}
