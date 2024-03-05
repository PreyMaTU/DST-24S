package dst.ass1.jpa.dao;

public interface IDAOFactory {

    IDriverDAO createDriverDAO();

    IEmploymentDAO createEmploymentDAO();

    ILocationDAO createLocationDAO();

    IMatchDAO createMatchDAO();

    IOrganizationDAO createOrganizationDAO();

    IRiderDAO createRiderDAO();

    ITripDAO createTripDAO();

    ITripInfoDAO createTripInfoDAO();

    IVehicleDAO createVehicleDAO();

    ITripReceiptDAO createTripReceiptDAO();

    IPaymentInfoDAO createPaymentInfoDAO();

}
