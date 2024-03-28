package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IVehicleDAO;
import dst.ass1.jpa.model.IVehicle;
import dst.ass1.jpa.model.impl.Vehicle;

import javax.persistence.EntityManager;
import java.util.List;

public class VehicleDAO implements IVehicleDAO {
    private final EntityManager entityManager;

    VehicleDAO(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

    @Override
    public IVehicle findById(Long id) {
        return entityManager.find(Vehicle.class, id);
    }

    @Override
    public List<IVehicle> findAll() {
        return entityManager
                .createQuery("SELECT v FROM Vehicle v", IVehicle.class)
                .getResultList();
    }
}
