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
}
