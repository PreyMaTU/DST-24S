package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.PaymentMethod;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PaymentInfo implements IPaymentInfo {

    static PaymentInfo fromIPaymentInfo( IPaymentInfo paymentInfo ) {
        return (PaymentInfo) paymentInfo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private PaymentMethod method;
    private Boolean preferred;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id= id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name= name;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return method;
    }

    @Override
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.method = paymentMethod;
    }

    @Override
    public Boolean isPreferred() {
        return preferred;
    }

    @Override
    public void setPreferred(Boolean preferred) {
        this.preferred= preferred;
    }
}
