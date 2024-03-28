package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.impl.Rider;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

public class RiderDAO implements IRiderDAO {
    private final EntityManager entityManager;

    RiderDAO(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

    @Override
    public IRider findById(Long id) {
        return entityManager.find(Rider.class, id);
    }

    @Override
    public List<IRider> findAll() {
        return entityManager
                .createQuery("SELECT r FROM Rider r", IRider.class)
                .getResultList();
    }

    @Override
    public List<IRider> findRidersWithNoTrips(Date start, Date end) {
        if( start == null || end == null ) {
            throw new IllegalArgumentException("Start and end of date range may not be null");
        }

        return entityManager
                .createNamedQuery(Constants.Q_INACTIVE_RIDER, IRider.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    public IRider findByEmail(String email) {
        try {
            return entityManager
                    .createNamedQuery(Constants.Q_RIDER_BY_EMAIL, IRider.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch( NoResultException e ) {
            return null;
        }
    }
}
