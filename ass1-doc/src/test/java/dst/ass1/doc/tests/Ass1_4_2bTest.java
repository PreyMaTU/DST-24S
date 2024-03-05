package dst.ass1.doc.tests;

import dst.ass1.doc.DocumentTestData;
import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.MongoService;
import dst.ass1.jpa.util.Constants;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;

public class Ass1_4_2bTest {

    @ClassRule
    public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();

    @Rule
    public MongoService mongo = new MongoService(new DocumentTestData());

    @Test
    public void findIdsByNameAndWithinPolygon_returnsCorrectLocationIdsForUWien() throws Exception {
        List<Long> locationIds =
            mongo
                .getDocumentQuery()
                .findDocumentsByNameWithinPolygon(
                    "U Wien",
                    List.of(
                        List.of(14., 46.),
                        List.of(17., 46.),
                        List.of(17., 50.),
                        List.of(14., 50.),
                        List.of(14., 46.)))
                .stream()
                .map(document -> document.getLong(Constants.I_LOCATION))
                .collect(Collectors.toList());
        assertEquals(4, locationIds.size());
        assertThat(locationIds, hasItems(1L, 12L, 13L, 14L));
    }

    @Test
    public void findIdsByNameAndWithinPolygon_returnsCorrectLocationIdsForMcD() throws Exception {
        List<Long> locationIds =
            mongo
                .getDocumentQuery()
                .findDocumentsByNameWithinPolygon(
                    "McD",
                    List.of(
                        List.of(14., 46.),
                        List.of(17., 46.),
                        List.of(17., 50.),
                        List.of(14., 50.),
                        List.of(14., 46.)))
                .stream()
                .map(document -> document.getLong(Constants.I_LOCATION))
                .collect(Collectors.toList());
        assertEquals(3, locationIds.size());
        assertThat(locationIds, hasItems(2L, 3L, 4L));
    }

    @Test
    public void findIdsByNameAndWithinPolygon_returnsNoResultForNotExistingName() throws Exception {
        List<Long> locationIds =
            mongo
                .getDocumentQuery()
                .findDocumentsByNameWithinPolygon(
                    "NoneExisting",
                    List.of(
                        List.of(14., 46.),
                        List.of(17., 46.),
                        List.of(17., 50.),
                        List.of(14., 50.),
                        List.of(14., 46.)))
                .stream()
                .map(document -> document.getLong(Constants.I_LOCATION))
                .collect(Collectors.toList());
        assertEquals(0, locationIds.size());
    }

    @Test
    public void findIdsByNameAndWithinPolygon_returnsNoResultForNoneInPolygon() throws Exception {
        List<Long> locationIds =
            mongo
                .getDocumentQuery()
                .findDocumentsByNameWithinPolygon(
                    "McD",
                    List.of(
                        List.of(14., 20.),
                        List.of(15., 20.),
                        List.of(15., 21.),
                        List.of(14., 21.),
                        List.of(14., 20.)))
                .stream()
                .map(document -> document.getLong(Constants.I_LOCATION))
                .collect(Collectors.toList());
        assertEquals(0, locationIds.size());
    }
}
