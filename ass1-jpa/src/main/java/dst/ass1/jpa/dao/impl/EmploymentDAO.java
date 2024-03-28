package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IEmploymentDAO;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.impl.Employment;

import javax.persistence.EntityManager;
import java.util.List;

public class EmploymentDAO implements IEmploymentDAO {
    private final EntityManager entityManager;

    EmploymentDAO(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

    @Override
    public IEmployment findById(Long id) {
        return entityManager.find(Employment.class, id);
    }

    @Override
    public List<IEmployment> findAll() {
        return entityManager
                .createQuery("SELECT e FROM Employment e", IEmployment.class)
                .getResultList();
    }
}
