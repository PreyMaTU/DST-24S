package dst.ass2.service.trip;

import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.tests.TestData;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootConfiguration
@Profile("testdata")
public class TestDataConfig implements ApplicationListener<ApplicationReadyEvent> {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public TestData testData() {
        return new TestData();
    }

    @Bean
    public TestDataInserter testDataInserter(TestData testData, IModelFactory modelFactory, PlatformTransactionManager transactionManager) {
        return new TestDataInserter(testData, modelFactory, transactionManager);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        event.getApplicationContext()
            .getBean(TestDataInserter.class)
            .insertTestData(em);
    }
}
