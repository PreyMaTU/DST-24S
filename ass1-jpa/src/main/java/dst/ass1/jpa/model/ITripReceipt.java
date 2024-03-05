package dst.ass1.jpa.model;

public interface ITripReceipt {

    Long getId();

    void setId(Long id);

    IMoney getTotal();

    void setTotal(IMoney total);

    IMoney getTip();

    void setTip(IMoney tip);

    boolean isPaid();

    void setPaid(boolean paid);

    IPaymentInfo getPaymentInfo();

    void setPaymentInfo(IPaymentInfo paymentInfo);

    ITripInfo getTripInfo();

    void setTripInfo(ITripInfo tripInfo);
}
