package dst.ass1.jooq.tests;

import dst.ass1.jooq.connection.DataSource;
import dst.ass1.jooq.dao.IRiderPreferenceDAO;
import dst.ass1.jooq.dao.impl.DAOFactory;
import dst.ass1.jooq.model.IModelFactory;
import dst.ass1.jooq.model.IRiderPreference;
import dst.ass1.jooq.model.impl.ModelFactory;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ass1_6_1Test {

    private static DSLContext dslContext;
    private static IRiderPreferenceDAO riderPreferenceDAO;
    private IRiderPreference riderPreference;
    private static IModelFactory modelFactory;

    @Before
    public void setup() {
        dslContext.execute("DELETE FROM PREFERENCE");
        dslContext.execute("DELETE FROM RIDER_PREFERENCE");

        riderPreference = modelFactory.createRiderPreference();
        riderPreference.setRiderId(1L);
        riderPreference.setArea("vienna");
        riderPreference.setVehicleClass("standard");
        riderPreference.setPreferences(Map.of("foo", "bar"));
    }

    @BeforeClass
    public static void setupAll() throws SQLException {
        dslContext = DataSource.getConnection();
        var factory = new DAOFactory(dslContext);
        riderPreferenceDAO = factory.createRiderPreferenceDao();
        modelFactory = new ModelFactory();
    }

    @Test
    public void insertsRider() {
        riderPreferenceDAO.insert(riderPreference);

        assertEquals(riderPreference, riderPreferenceDAO.findById(riderPreference.getRiderId()));
    }

    @Test
    public void deletesRider() {
        riderPreferenceDAO.insert(riderPreference);

        riderPreferenceDAO.delete(riderPreference.getRiderId());

        assertNull(riderPreferenceDAO.findById(riderPreference.getRiderId()));
    }

    @Test
    public void onlyDeletesSpecifiedRider() {
        var riderPreference2 = modelFactory.createRiderPreference();
        riderPreference2.setRiderId(2L);
        riderPreference2.setArea("Graz");
        riderPreference2.setVehicleClass("standard");
        riderPreference2.setPreferences(Map.of("foo", "bar"));

        riderPreferenceDAO.insert(riderPreference);
        riderPreferenceDAO.insert(riderPreference2);

        riderPreferenceDAO.delete(riderPreference.getRiderId());

        assertNull(riderPreferenceDAO.findById(riderPreference.getRiderId()));
        assertEquals(riderPreference2, riderPreferenceDAO.findById(riderPreference2.getRiderId()));
    }

    @Test
    public void deletesPreferencesAfterRiderWasDeleted() {
        riderPreferenceDAO.insert(riderPreference);

        riderPreferenceDAO.delete(riderPreference.getRiderId());

        var count = org.jooq.impl.DSL.field("count (*)", Integer.class);
        var select =
            dslContext
                .select(count)
                .from("preference p")
                .where("p.rider_id = " + riderPreference.getRiderId())
                .fetch();
        assertEquals(1, select.size());
        assertEquals(0, select.get(0).get(count).intValue());
    }

    @Test
    public void updatesPreferences() {
        riderPreferenceDAO.insert(riderPreference);

        riderPreference.setPreferences(Map.of("foo", "foo", "test", "test3"));
        riderPreferenceDAO.updatePreferences(riderPreference);

        assertEquals(riderPreference, riderPreferenceDAO.findById(riderPreference.getRiderId()));
    }

    @Test
    public void allowsToSearchAllPreferences() {
        var riderPreference2 = modelFactory.createRiderPreference();
        riderPreference2.setRiderId(2L);
        riderPreference2.setArea("Graz");
        riderPreference2.setVehicleClass("standard");
        riderPreference2.setPreferences(Map.of("foo", "bar"));

        riderPreferenceDAO.insert(riderPreference);
        riderPreferenceDAO.insert(riderPreference2);

        assertEquals(List.of(riderPreference, riderPreference2), riderPreferenceDAO.findAll());
    }
}
