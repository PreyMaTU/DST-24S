package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.dao.impl.DAOFactory;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.util.Constants;
import org.hibernate.Session;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.*;

/**
 * Tests the self reference of IOrganization.
 */
public class Ass1_1_1_10Test extends Ass1_TestBase {

    @Test
    public void testOrganizationSelfAssociation() {
        EntityManager em = orm.createEntityManager();
        IOrganizationDAO dao = new DAOFactory(em).createOrganizationDAO();

        IOrganization organization1 = dao.findById(testData.organization1Id);
        IOrganization organization2 = dao.findById(testData.organization2Id);
        IOrganization organization3 = dao.findById(testData.organization3Id);
        IOrganization organization4 = dao.findById(testData.organization4Id);
        IOrganization organization5 = dao.findById(testData.organization5Id);

        assertNotNull(organization1.getParts());
        assertNotNull(organization1.getPartOf());
        assertNotNull(organization2.getParts());
        assertNotNull(organization2.getPartOf());
        assertNotNull(organization3.getParts());
        assertNotNull(organization3.getPartOf());
        assertNotNull(organization4.getParts());
        assertNotNull(organization4.getPartOf());
        assertNotNull(organization5.getParts());
        assertNotNull(organization5.getPartOf());

        List<Long> organization1Parts = map(organization1.getParts(), IOrganization::getId);
        List<Long> organization1PartOf = map(organization1.getPartOf(), IOrganization::getId);
        List<Long> organization2Parts = map(organization2.getParts(), IOrganization::getId);
        List<Long> organization2PartOf = map(organization2.getPartOf(), IOrganization::getId);
        List<Long> organization3Parts = map(organization3.getParts(), IOrganization::getId);
        List<Long> organization3PartOf = map(organization3.getPartOf(), IOrganization::getId);
        List<Long> organization4Parts = map(organization4.getParts(), IOrganization::getId);
        List<Long> organization4PartOf = map(organization4.getPartOf(), IOrganization::getId);
        List<Long> organization5Parts = map(organization5.getParts(), IOrganization::getId);
        List<Long> organization5PartOf = map(organization5.getPartOf(), IOrganization::getId);


        assertThat(organization1Parts, hasItems(testData.organization4Id, testData.organization5Id));
        assertThat(organization1PartOf, hasItem(testData.organization2Id));
        assertThat(organization2Parts, hasItem(testData.organization1Id));
        assertThat(organization2PartOf, hasItems(testData.organization4Id));
        assertThat(organization3Parts.size(), is(0));
        assertThat(organization3PartOf.size(), is(0));
        assertThat(organization4Parts, hasItems(testData.organization2Id));
        assertThat(organization4PartOf, hasItems(testData.organization1Id));
        assertThat(organization5Parts.size(), is(0));
        assertThat(organization5PartOf, hasItems(testData.organization1Id));
    }

    @Test
    public void testOrganizationSelfAssociationJdbc() {
        String sql = "SELECT " + Constants.I_ORGANIZATION_PARTS + ", " + Constants.I_ORGANIZATION_PART_OF +
            " FROM " + Constants.J_ORGANIZATION_PARTS +
            " ORDER BY " + Constants.I_ORGANIZATION_PARTS + ", " + Constants.I_ORGANIZATION_PART_OF;

        em.unwrap(Session.class).doWork(connection -> {

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                assertTrue(rs.next());

                assertEquals((long) testData.organization1Id, rs.getLong(Constants.I_ORGANIZATION_PARTS));
                assertEquals((long) testData.organization4Id, rs.getLong(Constants.I_ORGANIZATION_PART_OF));

                assertTrue(rs.next());

                assertEquals((long) testData.organization1Id, rs.getLong(Constants.I_ORGANIZATION_PARTS));
                assertEquals((long) testData.organization5Id, rs.getLong(Constants.I_ORGANIZATION_PART_OF));

                assertTrue(rs.next());

                assertEquals((long) testData.organization2Id, rs.getLong(Constants.I_ORGANIZATION_PARTS));
                assertEquals((long) testData.organization1Id, rs.getLong(Constants.I_ORGANIZATION_PART_OF));

                assertTrue(rs.next());

                assertEquals((long) testData.organization4Id, rs.getLong(Constants.I_ORGANIZATION_PARTS));
                assertEquals((long) testData.organization2Id, rs.getLong(Constants.I_ORGANIZATION_PART_OF));

                rs.close();
            }
        });
    }

}
