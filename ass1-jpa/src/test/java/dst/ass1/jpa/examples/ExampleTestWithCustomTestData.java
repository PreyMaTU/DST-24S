package dst.ass1.jpa.examples;

import dst.ass1.jpa.ITestData;
import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.dao.IVehicleDAO;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.IVehicle;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This examples shows how you can write your own tests with custom fixtures that are injected it into the
 * {@link ORMService} rule. Using rules instead of abstract tests is considered good practice as it follows the
 * principle of composition over inheritance.
 */
public class ExampleTestWithCustomTestData {

    @Rule
    public ORMService orm = new ORMService(new MyTestData());

    @Test
    public void vehicleDAO_findAll_returnsCorrectValues() throws Exception {
        IVehicleDAO vehicleDAO = orm.getDaoFactory().createVehicleDAO();

        List<IVehicle> all = vehicleDAO.findAll();

        assertThat(all.isEmpty(), is(false));
        assertThat(all.size(), is(2));

        System.out.println(all);
    }

    @Test
    public void vehicleDAO_findById_returnsCorrectValue() throws Exception {
        IVehicleDAO vehicleDAO = orm.getDaoFactory().createVehicleDAO();

        IVehicle actual = vehicleDAO.findById(1L);

        assertThat(actual.getColor(), is("red"));
    }

    public static class MyTestData implements ITestData {

        @Override
        public void insert(IModelFactory modelFactory, EntityManager em) {
            IVehicle vehicle1 = modelFactory.createVehicle();
            IVehicle vehicle2 = modelFactory.createVehicle();

            vehicle1.setColor("red");
            vehicle1.setLicense("license1");
            vehicle1.setType("type_1");

            vehicle2.setColor("blue");
            vehicle2.setLicense("license2");
            vehicle2.setType("type_2");

            em.persist(vehicle1);
            em.persist(vehicle2);

            em.flush(); // transaction is done through the ORMTestFixture
        }
    }
}
