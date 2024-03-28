package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.impl.Driver;

import javax.persistence.EntityManager;
import java.util.List;

public class DriverDAO implements IDriverDAO {
    private final EntityManager entityManager;

    DriverDAO(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

    @Override
    public IDriver findById(Long id) {
        return entityManager.find(Driver.class, id);
    }

    @Override
    public List<IDriver> findAll() {
        return entityManager
                .createQuery("SELECT d FROM Driver d", IDriver.class)
                .getResultList();
    }
}
