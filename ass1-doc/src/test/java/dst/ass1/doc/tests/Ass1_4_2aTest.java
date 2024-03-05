package dst.ass1.doc.tests;

import dst.ass1.doc.DocumentTestData;
import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.doc.MongoService;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Ass1_4_2aTest {

    @ClassRule
    public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();

    @Rule
    public MongoService mongo = new MongoService(new DocumentTestData());

    @Test
    public void findByLocationType_returnsCorrectDocumentsForTypePlace() {
        IDocumentQuery documentQuery = mongo.getDocumentQuery();

        var locations = documentQuery.findDocumentsByType("place");
        assertNotNull(locations);
        assertEquals(16L, locations.size());
        for (var location : locations) {
            assertNotNull(location.getLong(Constants.I_LOCATION));
            assertNotNull(location.getString(Constants.M_LOCATION_NAME));
            assertEquals("place", location.getString("type"));
            assertNotNull(location.getString("category"));
            assertNotNull(location.getInteger("closingHour"));
            assertNotNull(location.getInteger("openHour"));

            Document geo = location.get("geo", Document.class);
            assertNotNull(geo);
            List<Double> coordinates = geo.getList("coordinates", Double.class);
            assertNotNull(coordinates);
            assertEquals(2, coordinates.size());
            assertEquals("Point", geo.getString("type"));
        }
    }

    @Test
    public void findByLocationType_returnsCorrectDocumentsForTypeFood() {
        IDocumentQuery documentQuery = mongo.getDocumentQuery();

        var foods = documentQuery.findDocumentsByType("food");
        assertNotNull(foods);
        assertEquals(1L, foods.size());
        var food = foods.get(0);
        assertEquals("food", food.getString(("type")));
        assertEquals("Pommes", food.getString(("name")));
        assertEquals(List.of("potato", "salt", "oil"), food.getList("ingredients", String.class));
        assertEquals("food", food.getString(("type")));
    }
}
