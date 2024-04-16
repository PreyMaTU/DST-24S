package dst.ass2.service.trip;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.impl.DAOFactory;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.impl.ModelFactory;
import dst.ass1.jpa.util.Constants;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootConfiguration
public class TripApplicationConfig {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public ResourceConfig jerseyConfig() {
        return new ResourceConfig()
            .packages("dst.ass2.service.trip");
    }

    @Bean
    public IModelFactory modelFactory() {
        return new ModelFactory();
    }

    @Bean
    public IDAOFactory daoFactory() {
        return new DAOFactory(em);
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

}
