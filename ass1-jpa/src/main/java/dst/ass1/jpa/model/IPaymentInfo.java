package dst.ass1.jpa.model;

public interface IPaymentInfo {


    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    PaymentMethod getPaymentMethod();

    void setPaymentMethod(PaymentMethod paymentMethod);

    Boolean isPreferred();

    void setPreferred(Boolean preferred);
}
