package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.impl.Organization;

import javax.persistence.EntityManager;
import java.util.List;

public class OrganizationDAO implements IOrganizationDAO {
    private final EntityManager entityManager;

    OrganizationDAO(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

    @Override
    public IOrganization findById(Long id) {
        return entityManager.find(Organization.class, id);
    }

    @Override
    public List<IOrganization> findAll() {
        return entityManager
                .createQuery("SELECT o FROM Organization o", IOrganization.class)
                .getResultList();
    }
}
