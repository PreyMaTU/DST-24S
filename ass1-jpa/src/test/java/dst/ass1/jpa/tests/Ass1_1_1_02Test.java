package dst.ass1.jpa.tests;

import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.util.Constants;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests the IVehicle license unique constraint.
 */
public class Ass1_1_1_02Test {

    @Rule
    public ORMService orm = new ORMService();

    @Test
    public void testConstraint() {
        new UniqueConstraintTester<>(() -> orm.getModelFactory().createVehicle(), e -> e.setLicense("uniquevalue"))
            .run(orm.getEntityManager());
    }

    @Test
    public void testConstraintJdbc() {
        assertTrue(orm.getDatabaseGateway().isIndex(Constants.T_VEHICLE, Constants.M_VEHICLE_LICENSE, false));
        assertTrue(orm.getDatabaseGateway().isNullable(Constants.T_VEHICLE, Constants.M_VEHICLE_LICENSE));
    }

}
