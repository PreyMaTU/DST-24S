package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.util.Constants;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Query;

import static org.junit.Assert.*;

public class Ass1_2_1aTest extends Ass1_TestBase {

    private IRiderDAO dao;
    private Class<? extends IRider> entityClass;

    @Before
    public void setUp() throws Exception {
        dao = daoFactory.createRiderDAO();
        entityClass = modelFactory.createRider().getClass();
    }

    @Test
    public void namedQuery_withEmailParameter_returnsNonEmptyResultSet() throws Exception {
        Query query = em.createNamedQuery(Constants.Q_RIDER_BY_EMAIL);

        try {
            query.setParameter("email", TestData.RIDER_1_EMAIL);
        } catch (IllegalArgumentException e) {
            throw new AssertionError("Could not set parameter 'email' in named query " + Constants.Q_RIDER_BY_EMAIL, e);
        }

        assertEquals("Expected exactly one result", 1, query.getResultList().size());
    }

    @Test
    public void findByEmail_returnsCorrectResult() throws Exception {
        Long riderId = testData.rider1Id;

        IRider actual = dao.findByEmail(TestData.RIDER_1_EMAIL);
        IRider expected = em.find(entityClass, riderId);

        assertEquals(riderId, actual.getId());
        assertSame(expected, actual); // note that they should actually be the same object due EntityManager caching!
    }

    @Test
    public void findByEmail_withNonExistingEmail_returnsNull() throws Exception {
        assertNull(dao.findByEmail("non@existing.com"));
    }
}
