package dst.ass1.jpa;

import dst.ass1.jpa.model.IModelFactory;

import javax.persistence.EntityManager;

// DO NOT MODIFY THIS CLASS.

/**
 * The ITestData interface is used by the {@link ORMService} to insert test data before each test run. You can pass
 * custom implementation of {@link ITestData} to the constructor of {@link ORMService} to create your own test fixture.
 */
public interface ITestData {
    /**
     * Creates test data using the model factory and inserts them into the entity manager.
     *
     * @param modelFactory the model factory
     * @param em           the entity manager
     */
    void insert(IModelFactory modelFactory, EntityManager em);
}
