package dst.ass1.jpa.tests;

import dst.ass1.jpa.ITestData;
import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.dao.*;
import dst.ass1.jpa.model.*;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityTransaction;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.object.IsCompatibleType.typeCompatibleWith;

/**
 * Tests the basic setup of the model and dao factory (makes sure they don't return null), and also tests whether all
 * relevant entities have been mapped at all and test data can be inserted.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Ass1_1_1_00Test {


    @ClassRule
    public static ORMService orm = new ORMService();

    @Rule
    public ErrorCollector err = new ErrorCollector();

    @Test
    public void test01_modelFactoryReturnsThings() {
        IModelFactory modelFactory = orm.getModelFactory();

        err.checkThat(modelFactory.createDriver(), isA(IDriver.class));
        err.checkThat(modelFactory.createEmployment(), isA(IEmployment.class));
        err.checkThat(modelFactory.createEmploymentKey(), isA(IEmploymentKey.class));
        err.checkThat(modelFactory.createLocation(), isA(ILocation.class));
        err.checkThat(modelFactory.createMatch(), isA(IMatch.class));
        err.checkThat(modelFactory.createMoney(), isA(IMoney.class));
        err.checkThat(modelFactory.createOrganization(), isA(IOrganization.class));
        err.checkThat(modelFactory.createRider(), isA(IRider.class));
        err.checkThat(modelFactory.createTrip(), isA(ITrip.class));
        err.checkThat(modelFactory.createTripInfo(), isA(ITripInfo.class));
        err.checkThat(modelFactory.createVehicle(), isA(IVehicle.class));
        err.checkThat(modelFactory.createPaymentInfo(), isA(IPaymentInfo.class));
        err.checkThat(modelFactory.createTripReceipt(), isA(ITripReceipt.class));
    }

    @Test
    public void test02_daoFactoryReturnsThings() {
        IDAOFactory daoFactory = orm.getDaoFactory();

        err.checkThat(daoFactory.createDriverDAO(), isA(IDriverDAO.class));
        err.checkThat(daoFactory.createEmploymentDAO(), isA(IEmploymentDAO.class));
        err.checkThat(daoFactory.createLocationDAO(), isA(ILocationDAO.class));
        err.checkThat(daoFactory.createMatchDAO(), isA(IMatchDAO.class));
        err.checkThat(daoFactory.createOrganizationDAO(), isA(IOrganizationDAO.class));
        err.checkThat(daoFactory.createRiderDAO(), isA(IRiderDAO.class));
        err.checkThat(daoFactory.createTripDAO(), isA(ITripDAO.class));
        err.checkThat(daoFactory.createTripInfoDAO(), isA(ITripInfoDAO.class));
        err.checkThat(daoFactory.createVehicleDAO(), isA(IVehicleDAO.class));
        err.checkThat(daoFactory.createPaymentInfoDAO(), isA(IPaymentInfoDAO.class));
        err.checkThat(daoFactory.createTripReceiptDAO(), isA(ITripReceiptDAO.class));
    }

    @Test
    public void test03_entityTypesManagedCorrectly() throws Exception {
        List<Class<?>> types = orm.getDatabaseGateway().getManagedJavaTypes();

        assertThat("No managed types found", types.isEmpty(), is(false));
        System.out.println("Managed types: " + types);
        err.checkThat(types, hasItem(typeCompatibleWith(IDriver.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(IEmployment.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(IEmploymentKey.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(ILocation.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(IMoney.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(IOrganization.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(IRider.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(ITrip.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(ITripInfo.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(IVehicle.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(ITripReceipt.class)));
        err.checkThat(types, hasItem(typeCompatibleWith(IPaymentInfo.class)));
    }

    @Test
    public void test04_canInsertTestFixture() throws Exception {
        ITestData testData = new TestData();

        EntityTransaction tx = orm.em().getTransaction();
        tx.begin();
        try {
            testData.insert(orm.getModelFactory(), orm.em());
        } catch (Exception e) {
            throw new AssertionError("Exception while inserting test fixture.", e);
        } finally {
            tx.rollback();
        }
    }


}
