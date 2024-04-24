package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IPaymentInfoDAO;
import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.impl.PaymentInfo;

import javax.persistence.EntityManager;

public class PaymentInfoDAO implements IPaymentInfoDAO {
    private final EntityManager entityManager;

    PaymentInfoDAO( EntityManager entityManager ) {
        this.entityManager= entityManager;
    }

    @Override
    public IPaymentInfo findPreferredOrAnyPaymentInfoByRider(Long riderId) {
        final var results= entityManager
                .createQuery(
                        "SELECT p FROM Rider r JOIN r.paymentInfos p " +
                                "WHERE r.id = :riderId " +
                                "ORDER BY p.preferred DESC "
                , IPaymentInfo.class)
                .setParameter("riderId", riderId)
                .setMaxResults(1)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }
}
