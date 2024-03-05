package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.*;

import javax.persistence.EntityManager;

public class DAOFactory implements IDAOFactory {

    private EntityManager em;

    public DAOFactory(EntityManager em) {
        this.em = em;
    }

    @Override
    public IDriverDAO createDriverDAO() {
        // TODO
        return null;
    }

    @Override
    public IEmploymentDAO createEmploymentDAO() {
        // TODO
        return null;
    }

    @Override
    public ILocationDAO createLocationDAO() {
        // TODO
        return null;
    }

    @Override
    public IMatchDAO createMatchDAO() {
        // TODO
        return null;
    }

    @Override
    public IOrganizationDAO createOrganizationDAO() {
        // TODO
        return null;
    }

    @Override
    public IRiderDAO createRiderDAO() {
        // TODO
        return null;
    }

    @Override
    public ITripDAO createTripDAO() {
        // TODO
        return null;
    }

    @Override
    public ITripInfoDAO createTripInfoDAO() {
        // TODO
        return null;
    }

    @Override
    public IVehicleDAO createVehicleDAO() {
        // TODO
        return null;
    }

    @Override
    public ITripReceiptDAO createTripReceiptDAO() {
        // TODO
        return null;
    }

    @Override
    public IPaymentInfoDAO createPaymentInfoDAO() {
        // TODO
        return null;
    }
}
