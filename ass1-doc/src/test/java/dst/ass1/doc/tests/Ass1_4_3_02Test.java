package dst.ass1.doc.tests;

import dst.ass1.doc.DocumentTestData;
import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.MongoService;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Ass1_4_3_02Test {

    @ClassRule
    public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();

    @Rule
    public MongoService mongo = new MongoService(new DocumentTestData());

    @Test
    public void getAverageOpeningHoursOfRestaurants_returnsCorrectAvgOpeningHours() throws Exception {
        List<Document> documentStatistics =
            mongo.getDocumentQuery().getAverageOpeningHoursPerCategory();
        assertNotNull(documentStatistics);
        assertEquals(4, documentStatistics.size());

        Map<String, Double> avgOpeningHoursMap =
            documentStatistics.stream()
                .collect(
                    Collectors.toMap(d -> d.getString("_id"), d -> d.getDouble("value")));
        assertThat(
            "expected four aggregation keys",
            avgOpeningHoursMap.keySet(),
            hasItems("University", "Restaurant", "Museum", "Park"));

        assertEquals(8.0, avgOpeningHoursMap.get("University"), 0.1);
        assertEquals(15.2, avgOpeningHoursMap.get("Restaurant"), 0.1);
        assertEquals(8.33, avgOpeningHoursMap.get("Museum"), 0.1);
        assertEquals(10, avgOpeningHoursMap.get("Park"), 0.1);
    }
}
