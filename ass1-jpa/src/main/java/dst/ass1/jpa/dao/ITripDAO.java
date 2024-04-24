package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;

import java.util.List;

public interface ITripDAO extends GenericDAO<ITrip> {
    default String getEntityName() { return "Trip"; }

    /**
     * Finds a list of trips with the specified trip state.
     *
     * @param state The trip state to filter by.
     * @return A list of ITrip objects with the specified trip state.
     * @throws IllegalArgumentException if state is null
     */
    List<ITrip> findByStatus(TripState state);

    /**
     * Finds a list of trips that are still in progress (not cancelled or completed)
     * for a specific driver.
     *
     * @param driverId The id of the driver to look for trips
     * @return A list of ITrip objects of a driver that are in progress
     */
    List<ITrip> findInProgressTripsByDriver(Long driverId);
}
