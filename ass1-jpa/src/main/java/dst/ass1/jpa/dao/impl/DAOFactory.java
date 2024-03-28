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
        return new DriverDAO( this.em );
    }

    @Override
    public IEmploymentDAO createEmploymentDAO() {
        return new EmploymentDAO( this.em );
    }

    @Override
    public ILocationDAO createLocationDAO() {
        return new LocationDAO( this.em );
    }

    @Override
    public IMatchDAO createMatchDAO() {
        return new MatchDAO( this.em );
    }

    @Override
    public IOrganizationDAO createOrganizationDAO() {
        return new OrganizationDAO( this.em );
    }

    @Override
    public IRiderDAO createRiderDAO() {
        return new RiderDAO( this.em );
    }

    @Override
    public ITripDAO createTripDAO() {
        return new TripDAO( this.em );
    }

    @Override
    public ITripInfoDAO createTripInfoDAO() {
        return new TripInfoDAO( this.em );
    }

    @Override
    public IVehicleDAO createVehicleDAO() {
        return new VehicleDAO( this.em );
    }

    @Override
    public ITripReceiptDAO createTripReceiptDAO() {
        return new TripReceiptDAO( this.em );
    }

    @Override
    public IPaymentInfoDAO createPaymentInfoDAO() {
        return new PaymentInfoDAO();
    }
}
