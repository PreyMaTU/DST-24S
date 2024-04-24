package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.impl.PaymentInfo;

public interface IPaymentInfoDAO {
    default String getEntityName() { return "Payment"; }

    /**
     * Finds a payment info object of a rider. It takes the rider's preferred
     * payment info, or any if none is set to be preferred.
     *
     * @param riderId The id of the rider to take the payment info of
     * @return A payment info of a rider
     */
    IPaymentInfo findPreferredOrAnyPaymentInfoByRider(Long riderId );
}
