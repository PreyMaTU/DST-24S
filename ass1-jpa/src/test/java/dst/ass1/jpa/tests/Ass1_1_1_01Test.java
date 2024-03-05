package dst.ass1.jpa.tests;

import dst.ass1.jpa.DatabaseGateway;
import dst.ass1.jpa.ORMService;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.runners.MethodSorters;

import static dst.ass1.jpa.CaseInsensitiveStringCollectionMatcher.hasItems;
import static dst.ass1.jpa.util.Constants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 * Tests the basic object-relational mapping by examining the created database tables and
 * constraints.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Ass1_1_1_01Test {

    @ClassRule
    public static ORMService orm = new ORMService();

    @Rule
    public ErrorCollector err = new ErrorCollector();

    private DatabaseGateway db;

    @Before
    public void setUp() throws Exception {
        db = orm.getDatabaseGateway();
    }

    @Test
    public void printTables() throws Exception {
        // not a test, just some systout output that may help you to gain insight into the created
        // database schema
        for (String table : db.getTables()) {
            System.out.printf("%-30s %s%n", table, db.getColumns(table));
        }
    }

    @Test
    public void testBasicTablesJdbc() throws Exception {
        // checks that all basic tables exist
        err.checkThat(
            db.getTables(),
            hasItems(
                T_RIDER,
                T_TRIP,
                T_LOCATION,
                T_TRIP_INFO,
                T_DRIVER,
                T_EMPLOYMENT,
                T_ORGANIZATION,
                T_VEHICLE,
                T_TRIP_RECEIPT,
                T_PAYMENT_INFO));
    }

    @Test
    public void testRelation01Jdbc() throws Exception {
        // driver -> vehicle
        err.checkThat(db.getColumns(T_DRIVER), hasItems(I_VEHICLE));
        err.checkThat(db.isNullable(T_DRIVER, I_VEHICLE), is(false));

        err.checkThat(db.getTables(), not(hasItems(J_DRIVER_VEHICLE)));
    }

    @Test
    public void testRelation02Jdbc() throws Exception {
        // rider <-> trip
        err.checkThat(db.getColumns(T_TRIP), hasItems(I_RIDER));
    }

    @Test
    public void testRelation03Jdbc() throws Exception {
        // driver <- employment -> organization

        err.checkThat(db.getColumns(T_EMPLOYMENT), hasItems(I_DRIVER, I_ORGANIZATION));
    }

    @Test
    public void testRelation04Jdbc() throws Exception {
        // trip <-> match -> vehicle
        //            |
        //            v
        //          driver

        err.checkThat(db.getColumns(T_MATCH), hasItems(I_DRIVER, I_TRIP, I_VEHICLE));
        err.checkThat(db.getColumns(T_DRIVER), not(hasItems(I_MATCH)));
        err.checkThat(db.getColumns(T_TRIP), not(hasItems(I_MATCH)));
        err.checkThat(db.getColumns(T_VEHICLE), not(hasItems(I_MATCH)));

        err.checkThat(db.getColumns(T_MATCH), hasItems(I_TRIP));
    }

    @Test
    public void testRelation05Jdbc() throws Exception {
        // organization <-> organization

        err.checkThat(
            "join table should be explicitly renamed!",
            db.getTables(),
            not(hasItems(T_ORGANIZATION + "_" + T_ORGANIZATION)));
        err.checkThat(db.getTables(), hasItems(J_ORGANIZATION_PARTS));
        err.checkThat(
            db.getColumns(J_ORGANIZATION_PARTS),
            hasItems(I_ORGANIZATION_PARTS, I_ORGANIZATION_PART_OF));
    }

    @Test
    public void testRelation06Jdbc() throws Exception {
        // trip -> location (stops)
        err.checkThat(db.getTables(), hasItems(J_TRIP_LOCATION));
        err.checkThat(db.getColumns(J_TRIP_LOCATION), hasItems(I_TRIP));
        err.checkThat(db.getColumns(J_TRIP_LOCATION), hasItems(I_STOPS));
    }

    @Test
    public void testRelation07Jdbc() throws Exception {
        // trip -> location (pickup)

        err.checkThat(db.getColumns(T_TRIP), hasItems(I_PICKUP));
        err.checkThat(db.isIndex(T_TRIP, I_PICKUP, true), is(true));
        err.checkThat(db.isNullable(T_TRIP, I_PICKUP), is(false));
    }

    @Test
    public void testRelation08Jdbc() throws Exception {
        // trip -> location (destination)
        err.checkThat(db.getColumns(T_TRIP), hasItems(I_DESTINATION));
        err.checkThat(db.isIndex(T_TRIP, I_DESTINATION, true), is(true));
        err.checkThat(db.isNullable(T_TRIP, I_DESTINATION), is(false));
    }

    @Test
    public void testRelation09Jdbc() throws Exception {
        // trip <-> tripinfo

        err.checkThat(db.getColumns(T_TRIP_INFO), hasItems(I_TRIP));
        err.checkThat(db.isNullable(T_TRIP_INFO, I_TRIP), is(false));
    }

    @Test
    public void testRelation10Jdbc() throws Exception {
        // organization -> vehicle

        err.checkThat(db.getTables(), hasItems(J_ORGANIZATION_VEHICLE));
        err.checkThat(db.getColumns(J_ORGANIZATION_VEHICLE), hasItems(I_ORGANIZATION, I_VEHICLES));
    }

    @Test
    public void testRelation11Jdbc() throws Exception {
        // Rider -> PaymentInfo

        err.checkThat(db.getTables(), hasItems(J_RIDER_PAYMENT_INFO));
        err.checkThat(db.getColumns(J_RIDER_PAYMENT_INFO), hasItems(I_RIDER, I_PAYMENT_INFOS));
    }

    @Test
    public void testRelation12Jdbc() throws Exception {
        // TripReceipt -> PaymentInfo

        err.checkThat(db.getColumns(T_TRIP_RECEIPT), hasItems(I_PAYMENT_INFO));
        err.checkThat(db.getColumns(T_PAYMENT_INFO), not(hasItems(I_TRIP_RECEIPT)));
    }

    @Test
    public void testRelation13Jdbc() throws Exception {
        // TripInfo <-> TripReceipt

        err.checkThat(db.getColumns(T_TRIP_INFO), hasItems(I_TRIP_RECEIPT));
        err.checkThat(db.getColumns(T_TRIP_RECEIPT), hasItems(I_TRIP_INFO));
    }


    @Test
    public void testColumnType01Jdbc() {
        err.checkThat(
            db.isColumnInTableWithType(T_RIDER, M_RIDER_PASSWORD, "BINARY VARYING", "20"), is(true));
    }
}
