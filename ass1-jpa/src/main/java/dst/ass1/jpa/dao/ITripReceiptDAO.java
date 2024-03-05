package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.PaymentMethod;
import dst.ass1.jpa.util.TupleResult;

import java.util.Date;
import java.util.List;

public interface ITripReceiptDAO {

    /**
     * Calculates the average tip per payment method within a given time range.
     * The result should be sorted descending by the average tip.
     *
     * @param start the start date of the time range, optional
     * @param end   the end date of the time range, optional
     */
    List<TupleResult<PaymentMethod, Double>> calculateAverageTipPerPaymentMethod(Date start, Date end);
}
