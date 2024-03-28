package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripReceiptDAO;
import dst.ass1.jpa.model.PaymentMethod;
import dst.ass1.jpa.model.impl.PaymentInfo;
import dst.ass1.jpa.model.impl.TripInfo;
import dst.ass1.jpa.model.impl.TripReceipt;
import dst.ass1.jpa.util.TupleResult;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TripReceiptDAO implements ITripReceiptDAO {
    private final EntityManager entityManager;

    TripReceiptDAO(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

    @Override
    public List<TupleResult<PaymentMethod, Double>> calculateAverageTipPerPaymentMethod(Date start, Date end) {
        final var criteriaBuilder= entityManager.getCriteriaBuilder();
        final var query= criteriaBuilder.createQuery( Object[].class );

        final var receipt= query.from(TripReceipt.class);
        final Join<TripReceipt, PaymentInfo> paymentInfo= receipt.join("paymentInfo");
        final Join<TripInfo, PaymentInfo> tripInfo= receipt.join("tripInfo");

        final var tipPercent=
                criteriaBuilder.avg(
                        criteriaBuilder.quot(
                                criteriaBuilder.prod(
                                        criteriaBuilder.literal(100),
                                        receipt.get("tip").get("currencyValue")
                                ),
                                receipt.get("total").get("currencyValue")
                        )
                ).alias("tip_percent");

        final var predicateCount= (start != null ? 1 : 0) + (end != null ? 1 : 0);
        final var predicates= new Predicate[ predicateCount ];
        if( start != null ) {
            predicates[0]= criteriaBuilder.greaterThan(tripInfo.get("completed"), start);
        }
        if( end != null ) {
            predicates[predicateCount-1]= criteriaBuilder.lessThan(tripInfo.get("completed"), end);
        }

        query
            .multiselect( paymentInfo.get("method"), tipPercent )
            .where(predicates)
            .groupBy( paymentInfo.get("method") )
            .orderBy( criteriaBuilder.desc( criteriaBuilder.literal(2) ) );

        return entityManager
                .createQuery( query )
                .getResultStream()
                .map( r -> new TupleResult<>((PaymentMethod) r[0], (Double) r[1]) )
                .collect(Collectors.toList());
    }
}
