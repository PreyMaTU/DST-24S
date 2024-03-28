package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IMoney;
import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.model.ITripReceipt;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;

@Entity
public class TripReceipt implements ITripReceipt {
    static TripReceipt fromITripReceipt( ITripReceipt tripReceipt ) {
        return (TripReceipt) tripReceipt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn
    private TripInfo tripInfo;

    @ManyToOne(optional = false)
    @JoinColumn
    private PaymentInfo paymentInfo;

    private Money total;

    @AttributeOverrides({
            @AttributeOverride(name = Constants.AO_NAME_TIP_CURRENCY, column = @Column(name= Constants.AO_COLUMN_NAME_TIP_CURRENCY)),
            @AttributeOverride(name = Constants.AO_NAME_TIP_CURRENCY_VALUE, column = @Column(name= Constants.AO_COLUMN_NAME_TIP_CURRENCY_VALUE))
    })
    private Money tip;
    private Boolean paid;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id= id;
    }

    @Override
    public IMoney getTotal() {
        return total;
    }

    @Override
    public void setTotal(IMoney total) {
        this.total= Money.fromIMoney( total );
    }

    @Override
    public IMoney getTip() {
        return tip;
    }

    @Override
    public void setTip(IMoney tip) {
        this.tip= Money.fromIMoney( tip );
    }

    @Override
    public boolean isPaid() {
        return paid;
    }

    @Override
    public void setPaid(boolean paid) {
        this.paid= paid;
    }

    @Override
    public IPaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    @Override
    public void setPaymentInfo(IPaymentInfo paymentInfo) {
        this.paymentInfo= PaymentInfo.fromIPaymentInfo(paymentInfo);
    }

    @Override
    public ITripInfo getTripInfo() {
        return tripInfo;
    }

    @Override
    public void setTripInfo(ITripInfo tripInfo) {
        this.tripInfo= TripInfo.fromITripInfo(tripInfo);
    }
}
