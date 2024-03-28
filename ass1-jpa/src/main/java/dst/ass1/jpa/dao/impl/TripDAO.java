package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import java.util.List;

public class TripDAO implements ITripDAO {
    private final EntityManager entityManager;

    TripDAO( EntityManager entityManager ) {
        this.entityManager= entityManager;
    }


    @Override
    public ITrip findById(Long id) {
        return entityManager.find( Trip.class, id );
    }

    @Override
    public List<ITrip> findAll() {
        return entityManager
                .createQuery("SELECT t FROM Trip t", ITrip.class)
                .getResultList();
    }

    @Override
    public List<ITrip> findByStatus(TripState state) {
        if( state == null ) {
            throw new IllegalArgumentException("State may not be null");
        }

        return entityManager
                .createNamedQuery(Constants.Q_TRIP_BY_STATE, ITrip.class)
                .setParameter("state", state)
                .getResultList();
    }
}
