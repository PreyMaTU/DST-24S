package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

/**
 * Creates new instances of your model implementations.
 */
public class ModelFactory implements IModelFactory {

    @Override
    public IModelFactory createModelFactory() {
        return new ModelFactory();
    }

    @Override
    public IDriver createDriver() {
        return new Driver();
    }

    @Override
    public IEmployment createEmployment() {
        return new Employment();
    }

    @Override
    public IEmploymentKey createEmploymentKey() {
        return new EmploymentKey();
    }

    @Override
    public ILocation createLocation() {
        return new Location();
    }

    @Override
    public IMatch createMatch() {
        return new Match();
    }

    @Override
    public IMoney createMoney() {
        return new Money();
    }

    @Override
    public IOrganization createOrganization() {
        return new Organization();
    }

    @Override
    public IRider createRider() {
        return new Rider();
    }

    @Override
    public ITrip createTrip() {
        return new Trip();
    }

    @Override
    public ITripInfo createTripInfo() {
        return new TripInfo();
    }

    @Override
    public IVehicle createVehicle() {
        return new Vehicle();
    }

    @Override
    public ITripReceipt createTripReceipt() {
        return new TripReceipt();
    }

    @Override
    public IPaymentInfo createPaymentInfo() {
        return new PaymentInfo();
    }
}
