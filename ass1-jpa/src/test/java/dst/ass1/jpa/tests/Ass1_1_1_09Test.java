package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.dao.IVehicleDAO;
import dst.ass1.jpa.dao.impl.DAOFactory;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.IVehicle;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the association between IOrganization and IVehicle.
 */
public class Ass1_1_1_09Test extends Ass1_TestBase {

    @Test
    public void testOrganizationVehicleAssociation() {
        EntityManager em = orm.createEntityManager();
        DAOFactory daoFactory = new DAOFactory(em);

        IOrganizationDAO organizationDAO = daoFactory.createOrganizationDAO();
        IVehicleDAO vehicleDAO = daoFactory.createVehicleDAO();

        List<IVehicle> vehicles = vehicleDAO.findAll();

        assertNotNull(vehicles);
        assertEquals(4, vehicles.size());

        IVehicle vehicle1 = vehicleDAO.findById(testData.vehicle1Id);
        IVehicle vehicle2 = vehicleDAO.findById(testData.vehicle2Id);
        IVehicle vehicle3 = vehicleDAO.findById(testData.vehicle3Id);
        IVehicle vehicle4 = vehicleDAO.findById(testData.vehicle4Id);

        assertEquals(testData.vehicle1Id, vehicle1.getId());
        assertEquals(testData.vehicle2Id, vehicle2.getId());
        assertEquals(testData.vehicle3Id, vehicle3.getId());
        assertEquals(testData.vehicle4Id, vehicle4.getId());

        List<IOrganization> organizations = organizationDAO.findAll();

        assertNotNull(organizations);
        assertEquals(5, organizations.size());

        IOrganization organization1 = organizationDAO.findById(testData.organization1Id);
        IOrganization organization2 = organizationDAO.findById(testData.organization2Id);
        IOrganization organization3 = organizationDAO.findById(testData.organization3Id);
        IOrganization organization4 = organizationDAO.findById(testData.organization4Id);
        IOrganization organization5 = organizationDAO.findById(testData.organization5Id);

        assertEquals(testData.organization1Id, organization1.getId());
        assertNotNull(organization1.getVehicles());
        assertThat(organization1.getVehicles().size(), is(3));

        List<Long> vehicleIds1 = map(organization1.getVehicles(), IVehicle::getId);
        assertThat(vehicleIds1, hasItems(testData.vehicle1Id, testData.vehicle2Id, testData.vehicle3Id));

        assertEquals(testData.organization2Id, organization2.getId());
        assertNotNull(organization2.getVehicles());
        assertThat(organization2.getVehicles().size(), is(1));

        List<Long> vehicleIds2 = map(organization2.getVehicles(), IVehicle::getId);
        assertThat(vehicleIds2, hasItems(testData.vehicle4Id));

        assertEquals(testData.organization3Id, organization3.getId());
        assertNotNull(organization3.getVehicles());
        assertThat(organization3.getVehicles().size(), is(0));

        assertEquals(testData.organization4Id, organization4.getId());
        assertNotNull(organization4.getVehicles());
        assertThat(organization4.getVehicles().size(), is(0));

        assertEquals(testData.organization5Id, organization5.getId());
        assertNotNull(organization5.getVehicles());
        assertThat(organization5.getVehicles().size(), is(0));
    }

}
