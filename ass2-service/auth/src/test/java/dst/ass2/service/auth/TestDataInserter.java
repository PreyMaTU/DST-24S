package dst.ass2.service.auth;

import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.tests.TestData;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

public class TestDataInserter {

    private final PlatformTransactionManager transactionManager;
    private final IModelFactory modelFactory;
    private final TestData testData;

    public TestDataInserter(TestData testData, IModelFactory modelFactory, PlatformTransactionManager transactionManager) {
        this.testData = testData;
        this.modelFactory = modelFactory;
        this.transactionManager = transactionManager;
    }

    public void insertTestData(EntityManager em) {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            testData.insert(modelFactory, em);
            return null;
        });
    }

}
