package dst.ass2.service.trip;

import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.tests.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

public class TestDataInserter {
    private static final Logger LOG = LoggerFactory.getLogger(TestDataInserter.class);

    private final PlatformTransactionManager transactionManager;
    private final IModelFactory modelFactory;
    private final TestData testData;

    public TestDataInserter(TestData testData, IModelFactory modelFactory, PlatformTransactionManager transactionManager) {
        this.testData = testData;
        this.modelFactory = modelFactory;
        this.transactionManager = transactionManager;
    }

    public void insertTestData(EntityManager em) {
        LOG.info("Inserting test data...");
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            testData.insert(modelFactory, em);
            return null;
        });
    }

}
