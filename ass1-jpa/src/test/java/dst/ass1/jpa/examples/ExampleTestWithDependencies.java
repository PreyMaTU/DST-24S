package dst.ass1.jpa.examples;

import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.IVehicleDAO;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.IVehicle;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This test has no test fixture and dependencies between tests. This is possible by declaring {@link ORMService} as a
 * static class-level rule via {@link ClassRule}.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleTestWithDependencies {

    @ClassRule
    public static ORMService orm = new ORMService();

    @Test
    public void t01_insert() throws Exception {
        EntityManager em = orm.getEntityManager();
        IModelFactory modelFactory = orm.getModelFactory();

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


        em.getTransaction().begin();
        em.flush();
        em.getTransaction().commit();

        assertThat(vehicle1.getId(), notNullValue());
        assertThat(vehicle2.getId(), notNullValue());
    }

    @Test
    public void t02_query() throws Exception {
        IDAOFactory daoFactory = orm.getDaoFactory();
        IVehicleDAO vehicleDAO = daoFactory.createVehicleDAO();

        List<IVehicle> all = vehicleDAO.findAll();

        assertThat(all.isEmpty(), is(false));
        assertThat(all.size(), is(2));

        System.out.println(all);
    }
}
