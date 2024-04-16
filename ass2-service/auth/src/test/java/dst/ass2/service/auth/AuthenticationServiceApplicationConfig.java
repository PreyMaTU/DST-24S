package dst.ass2.service.auth;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.impl.DAOFactory;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.impl.ModelFactory;
import dst.ass1.jpa.tests.TestData;
import dst.ass1.jpa.util.Constants;
import dst.ass2.service.auth.grpc.GrpcServerProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootConfiguration
@PropertySource("classpath:/dst/ass2/service/auth/grpc.properties")
public class AuthenticationServiceApplicationConfig {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public IModelFactory modelFactory() {
        return new ModelFactory();
    }

    @Bean
    public IDAOFactory daoFactory() {
        return new DAOFactory(em);
    }

    @Bean
    public GrpcServerProperties grpcServerProperties(@Value("${grpc.port}") int port) {
        return new GrpcServerProperties(port);
    }

    @Bean
    @Profile("grpc")
    public SpringGrpcServerRunner springGrpcServerRunner() {
        return new SpringGrpcServerRunner();
    }

    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalEntityManagerFactoryBean bean = new LocalEntityManagerFactoryBean();
        bean.setPersistenceUnitName(Constants.JPA_PERSISTENCE_UNIT);
        // fixes collection proxy problem when using jersey
        bean.getJpaPropertyMap().put("hibernate.enable_lazy_load_no_trans", true);
        return bean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalEntityManagerFactoryBean entityManagerFactoryBean) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setPersistenceUnitName(Constants.JPA_PERSISTENCE_UNIT);
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return transactionManager;
    }

    @Bean
    @Profile("testdata")
    public TestData testData() {
        return new TestData();
    }

    @Bean
    @Profile("testdata")
    public TestDataInserter testDataInserter(TestData testData, IModelFactory modelFactory, PlatformTransactionManager transactionManager) {
        return new TestDataInserter(testData, modelFactory, transactionManager);
    }

    @Bean
    @Profile("testdata")
    public AuthServiceDataInjector dataInjector(TestDataInserter testDataInserter) {
        return new AuthServiceDataInjector(em, testDataInserter);
    }

    /**
     * Makes sure data is in the database before the {@link ICachingAuthenticationService} is initialized.
     */
    public static class AuthServiceDataInjector implements BeanPostProcessor {
        private boolean dataInjected = false;

        private final EntityManager em;
        private final TestDataInserter testDataInserter;

        public AuthServiceDataInjector(EntityManager em, TestDataInserter testDataInserter) {
            this.em = em;
            this.testDataInserter = testDataInserter;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (!dataInjected && (bean instanceof ICachingAuthenticationService)) {
                testDataInserter.insertTestData(em);
                dataInjected = true;
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }

}
