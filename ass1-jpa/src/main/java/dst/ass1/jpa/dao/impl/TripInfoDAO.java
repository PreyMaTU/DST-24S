package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripInfoDAO;
import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.model.impl.TripInfo;
import dst.ass1.jpa.util.Constants;
import dst.ass1.jpa.util.TupleResult;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class TripInfoDAO implements ITripInfoDAO {
    private final EntityManager entityManager;

    TripInfoDAO(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

    @Override
    public ITripInfo findById(Long id) {
        return entityManager.find(TripInfo.class, id);
    }

    @Override
    public List<ITripInfo> findAll() {
        return entityManager
                .createQuery("SELECT t FROM TripInfo t", ITripInfo.class)
                .getResultList();
    }

    @Override
    public List<TupleResult<Long, Double>> findRidersAverageRating() {
        return entityManager
                .createNamedQuery(Constants.Q_AVERAGE_RATING_RIDER, Object[].class )
                .getResultStream()
                .map( result -> new TupleResult<>((Long)result[0], (Double)result[1]))
                .collect(Collectors.toList());
    }
}
