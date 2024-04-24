package dst.ass1.jpa.dao;

public interface IPaymentInfoDAO {
    default String getEntityName() { return "Payment"; }
}
