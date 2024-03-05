package dst.ass1.jpa.tests;

import dst.ass1.jpa.DatabaseGateway;
import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.model.IModelFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Ass1_TestBase {

    protected final TestData testData = new TestData();

    @Rule
    public ORMService orm = new ORMService(testData);

    @Rule
    public ErrorCollector err = new ErrorCollector();

    // commonly used classes unwrapped from ORMService
    protected EntityManager em;
    protected IModelFactory modelFactory;
    protected IDAOFactory daoFactory;
    protected DatabaseGateway db;

    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> fn) {
        return collection.stream().map(fn).collect(Collectors.toList());
    }

    @Before
    public void setUpBase() throws Exception {
        em = orm.getEntityManager();
        modelFactory = orm.getModelFactory();
        daoFactory = orm.getDaoFactory();
        db = orm.getDatabaseGateway();
    }

}
