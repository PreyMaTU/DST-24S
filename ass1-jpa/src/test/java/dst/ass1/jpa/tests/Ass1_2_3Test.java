package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.ITripReceiptDAO;
import dst.ass1.jpa.model.PaymentMethod;
import dst.ass1.jpa.util.TupleResult;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import static dst.ass1.jpa.tests.TestData.localDateToDate;
import static org.junit.Assert.assertEquals;

public class Ass1_2_3Test extends Ass1_TestBase {

    private ITripReceiptDAO tripReceiptDao;

    @Before
    public void setUp() throws Exception {
        tripReceiptDao = daoFactory.createTripReceiptDAO();
    }

    @Test
    public void test_calculateAverageTipPerPaymentMethod_shouldReturnEmptyList() {
        Date start = Date.from(Instant.ofEpochSecond(788914800));
        Date end = Date.from(Instant.ofEpochSecond(789001200));

        var result = tripReceiptDao.calculateAverageTipPerPaymentMethod(start, end);
        assertEquals(0, result.size());
    }

    @Test
    public void test_calculateAverageTipPerPaymentMethod_calculatesAverage_withStart() {
        LocalDate now = LocalDate.now();
        Date start = localDateToDate(now.minusDays(11));

        var result = tripReceiptDao.calculateAverageTipPerPaymentMethod(start, null);
        assertEquals(2, result.size());
        TupleResult<PaymentMethod, Double> expectedCash = new TupleResult<>(PaymentMethod.CASH, 6.0);
        assertEquals(expectedCash.getFirst(), result.get(0).getFirst());
        assertEquals(expectedCash.getSecond(), result.get(0).getSecond(), 0.1);

        TupleResult<PaymentMethod, Double> expectedPaypal = new TupleResult<>(PaymentMethod.PAYPAL, 0.0);
        assertEquals(expectedPaypal.getFirst(), result.get(1).getFirst());
        assertEquals(expectedPaypal.getSecond(), result.get(1).getSecond(), 0);
    }

    @Test
    public void test_calculateAverageTipPerPaymentMethod_calculatesAverage_withoutStartAndEnd() {
        var result = tripReceiptDao.calculateAverageTipPerPaymentMethod(null, null);
        assertEquals(3, result.size());

        TupleResult<PaymentMethod, Double> expectedCreditCard = new TupleResult<>(PaymentMethod.CREDIT_CARD, 10.766);
        assertEquals(expectedCreditCard.getFirst(), result.get(0).getFirst());
        assertEquals(expectedCreditCard.getSecond(), result.get(0).getSecond(), 0.1);

        TupleResult<PaymentMethod, Double> expectedCash = new TupleResult<>(PaymentMethod.CASH, 6.0);
        assertEquals(expectedCash.getFirst(), result.get(1).getFirst());
        assertEquals(expectedCash.getSecond(), result.get(1).getSecond(), 0.1);

        TupleResult<PaymentMethod, Double> expectedPaypal = new TupleResult<>(PaymentMethod.PAYPAL, 2.272);
        assertEquals(expectedPaypal.getFirst(), result.get(2).getFirst());
        assertEquals(expectedPaypal.getSecond(), result.get(2).getSecond(), 0.1);
    }

    @Test
    public void test_calculateAverageTipPerPaymentMethod_calculatesAverage_withEnd() {
        LocalDate now = LocalDate.now();
        var end = localDateToDate(now.minusDays(12));
        var result = tripReceiptDao.calculateAverageTipPerPaymentMethod(null, end);
        assertEquals(2, result.size());

        TupleResult<PaymentMethod, Double> expectedCreditCard = new TupleResult<>(PaymentMethod.CREDIT_CARD, 10.766);
        assertEquals(expectedCreditCard.getFirst(), result.get(0).getFirst());
        assertEquals(expectedCreditCard.getSecond(), result.get(0).getSecond(), 0.1);


        TupleResult<PaymentMethod, Double> expectedPaypal = new TupleResult<>(PaymentMethod.PAYPAL, 4.545);
        assertEquals(expectedPaypal.getFirst(), result.get(1).getFirst());
        assertEquals(expectedPaypal.getSecond(), result.get(1).getSecond(), 0.1);
    }

    @Test
    public void test_calculateAverageTipPerPaymentMethod_calculatesAverage_withStartAndEnd() {
        LocalDate now = LocalDate.now();
        var start = localDateToDate(now.minusDays(22));
        var end = localDateToDate(now.minusDays(12));
        var result = tripReceiptDao.calculateAverageTipPerPaymentMethod(start, end);
        assertEquals(1, result.size());

        TupleResult<PaymentMethod, Double> expectedCreditCard = new TupleResult<>(PaymentMethod.CREDIT_CARD, 1.428);
        assertEquals(expectedCreditCard.getFirst(), result.get(0).getFirst());
        assertEquals(expectedCreditCard.getSecond(), result.get(0).getSecond(), 0.1);
    }
}
