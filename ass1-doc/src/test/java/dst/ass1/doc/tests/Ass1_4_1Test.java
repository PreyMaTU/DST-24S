package dst.ass1.doc.tests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.IDocumentRepository;
import dst.ass1.doc.MockLocation;
import dst.ass1.doc.MongoService;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Ass1_4_1Test {

    public static final String LOCATION_NAME_1 = "testName1";
    public static final String LOCATION_NAME_2 = "testName2";
    public static final Long LOCATION_ID_1 = 1L;
    public static final Long LOCATION_ID_2 = 2L;
    public static final Double GEO_1_1 = 16.3699;
    public static final Double GEO_1_2 = 48.199;
    public static final Double GEO_2_1 = 16.368528;
    public static final Double GEO_2_2 = 48.200939;
    @ClassRule
    public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();
    @Rule
    public MongoService mongo = new MongoService();
    private MockLocation l1;
    private MockLocation l2;
    private Map<String, Object> l1Properties;
    private Map<String, Object> l2Properties;
    private Map<String, Object> geo1;
    private Map<String, Object> geo2;

    @Before
    public void setUp() {
        l1 = new MockLocation();
        l1.setName(LOCATION_NAME_1);
        l1.setLocationId(LOCATION_ID_1);

        l2 = new MockLocation();
        l2.setName(LOCATION_NAME_2);
        l2.setLocationId(LOCATION_ID_2);


        l1Properties = new HashMap<>();

        geo1 = new HashMap<>();
        geo1.put("type", "Point");
        geo1.put("coordinates", Arrays.asList(GEO_1_1, GEO_1_2));
        l1Properties.put("type", "place");
        l1Properties.put("geo", geo1);
        l1Properties.put("1337", "7331");
        l1Properties.put("Foo", "Bar");
        l1Properties.put("Complex", Arrays.asList(1, 2, 3, 5, 8));

        l2Properties = new HashMap<>();

        geo2 = new HashMap<>();
        geo2.put("type", "Point");
        geo2.put("coordinates", Arrays.asList(GEO_2_1, GEO_2_2));
        l2Properties.put("type", "place");
        l2Properties.put("geo", geo2);
        l2Properties.put("123456", "654321");
        l2Properties.put("F00", "B@r");
        l2Properties.put("Complex2", Arrays.asList(4, 6, 7, 9));
    }

    @Test
    public void insert_insertsDocumentsCorrectly() throws Exception {
        IDocumentRepository documentRepository = mongo.getDocumentRepository();
        MongoDatabase mongoDatabase = mongo.getMongoDatabase();

        documentRepository.insert(l1, l1Properties);
        documentRepository.insert(l2, l2Properties);

        MongoCollection<Document> collection = mongoDatabase.getCollection(Constants.COLL_LOCATION_DATA);
        Map<Long, Document> map = MongoService.idMap(collection, d -> d.getLong(Constants.I_LOCATION));

        assertNotNull(map);
        assertEquals(2, map.size());

        Document document1 = map.get(1L);
        assertEquals(LOCATION_ID_1, document1.get(Constants.I_LOCATION));
        assertEquals(LOCATION_NAME_1, document1.get(Constants.M_LOCATION_NAME));

        assertEquals("7331", document1.get("1337"));
        assertEquals("Bar", document1.get("Foo"));
        assertEquals(Arrays.asList(1, 2, 3, 5, 8), document1.get("Complex"));

        assertEquals("place", document1.get("type"));

        Map<String, Object> geo1Retrieved = document1.get("geo", new HashMap<>());
        assertEquals(2, geo1Retrieved.size());
        assertEquals("Point", geo1Retrieved.get("type"));
        List<Double> coordinates1 = (List<Double>) geo1Retrieved.get("coordinates");
        assertEquals(2, coordinates1.size());
        assertThat(coordinates1, hasItems(GEO_1_1, GEO_1_2));

        Document document2 = map.get(2L);
        assertEquals(LOCATION_ID_2, document2.get(Constants.I_LOCATION));
        assertEquals(LOCATION_NAME_2, document2.get(Constants.M_LOCATION_NAME));

        assertEquals("654321", document2.get("123456"));
        assertEquals("B@r", document2.get("F00"));
        assertEquals(Arrays.asList(4, 6, 7, 9), document2.get("Complex2"));

        assertEquals("place", document2.get("type"));

        Map<String, Object> geo2Retrieved = document2.get("geo", new HashMap<>());
        assertEquals(2, geo2Retrieved.size());
        assertEquals("Point", geo2Retrieved.get("type"));
        List<Double> coordinates2 = (List<Double>) geo2Retrieved.get("coordinates");
        assertEquals(2, coordinates2.size());
        assertThat(coordinates2, hasItems(GEO_2_1, GEO_2_2));
    }
}
