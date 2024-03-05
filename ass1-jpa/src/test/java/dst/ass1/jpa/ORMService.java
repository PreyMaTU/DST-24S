package dst.ass1.jpa;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.impl.DAOFactory;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.impl.ModelFactory;
import dst.ass1.jpa.util.Constants;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

// DO NOT MODIFY THIS CLASS.

/**
 * The ORMService class is used as a JUnit rule that, before each test run,
 * a) creates an entity manager factory from the persistence unit referenced in {@link Constants#JPA_PERSISTENCE_UNIT},
 * b) creates an entity manager using the factory,
 * c) creates dao and model factory instances, and
 * d) creates a database gateway for the entity manager.
 * <p>
 * It provides methods to access these services, and it closes the entity manager and corresponding factory after each
 * test run. If you pass an {@link ITestData} instance to the constructor, the {@link ORMService} will also insert test
 * data via a transaction before each run, and truncate tables after each run.
 */
public class ORMService extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(ORMService.class);

    private static EntityManagerFactory emf;
    private EntityManager em;

    private DatabaseGateway databaseGateway;

    private IModelFactory modelFactory;
    private IDAOFactory daoFactory;

    private ITestData testData;
    private boolean insertTestData;
    private boolean truncateTables;

    /**
     * Creates a new ORMService. By default, the ORMService rule only creates the database connection and other
     * ORM facilities. If you want to also insert a test fixture, use {@link #ORMService(ITestData)}.
     */
    public ORMService() {
        this(null, false, false);
    }

    /**
     * Creates a new ORMService that also inserts the given {@link ITestData} before each run and truncates the tables
     * afterwards.
     *
     * @param testData the test data to insert
     */
    public ORMService(ITestData testData) {
        this(testData, true, true);
    }

    public ORMService(ITestData testData, boolean insertTestData, boolean truncateTables) {
        this.testData = testData;
        this.insertTestData = insertTestData;
        this.truncateTables = truncateTables;
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(Constants.JPA_PERSISTENCE_UNIT);
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public IModelFactory getModelFactory() {
        return modelFactory;
    }

    public IDAOFactory getDaoFactory() {
        return daoFactory;
    }

    public DatabaseGateway getDatabaseGateway() {
        return databaseGateway;
    }

    /**
     * Alias for {@link #getEntityManager()}.
     *
     * @return an entity manager
     */
    public EntityManager em() {
        return getEntityManager();
    }

    @Override
    protected void before() throws Throwable {
        LOG.debug("Creating EntityManagerFactory");
        emf = createEntityManagerFactory();

        LOG.debug("Creating EntityManager");
        em = emf.createEntityManager();
        databaseGateway = new DatabaseGateway(em);

        LOG.debug("Initializing factories");
        // initialize factories
        modelFactory = new ModelFactory();
        daoFactory = new DAOFactory(em);

        if (testData != null && insertTestData) {
            insertTestData(testData);
        }
    }

    @Override
    protected void after() {
        if (truncateTables) {
            truncateTables();
        }

        try {
            LOG.debug("Closing EntityManager");
            em.close();
        } catch (Exception e) {
            LOG.error("Error while closing entity manager", e);
        }

        try {
            LOG.debug("Closing EntityManagerFactory");
            emf.close();
        } catch (Exception e) {
            LOG.error("Error while closing entity manager factory", e);
        }
    }

    protected void insertTestData(ITestData data) {
        EntityTransaction tx = getEntityManager().getTransaction();

        tx.begin();
        try {
            data.insert(getModelFactory(), getEntityManager());
            tx.commit();
        } catch (Exception e) {
            try {
                if (tx.isActive()) {
                    tx.rollback();
                }
            } catch (Exception rollbackException) {
                LOG.error("Error while rolling back transaction after exception", rollbackException);
            } finally {
                throw new IllegalStateException("Couldn't insert fixture. Can't proceed with tests", e); // rethrow original exception
            }
        }
    }

    protected void truncateTables() {
        try {
            LOG.debug("Truncating database tables");
            getDatabaseGateway().truncateTables();
        } catch (Exception e) {
            LOG.error("Error while trying to truncate tables after test", e);
        }
    }

}
