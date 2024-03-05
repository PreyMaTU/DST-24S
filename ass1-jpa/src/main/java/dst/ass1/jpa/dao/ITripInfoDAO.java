package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.util.TupleResult;

import java.util.List;

public interface ITripInfoDAO extends GenericDAO<ITripInfo> {

    /**
     * Finds the average rating for each rider.
     * Result is sorted descending by the calculated rating.
     *
     * @return a list of TupleResult objects containing the rider's ID and their average rating
     */
    List<TupleResult<Long, Double>> findRidersAverageRating();
}
