package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.PaymentMethod;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PaymentInfo implements IPaymentInfo {

    static PaymentInfo fromIPaymentInfo( IPaymentInfo info ) {
        if( info == null ) {
            return null;
        }
        final var paymentInfo= new PaymentInfo();
        paymentInfo.setId(info.getId());
        paymentInfo.setPaymentMethod(info.getPaymentMethod());
        paymentInfo.setName(info.getName());
        paymentInfo.setPreferred(info.isPreferred());
        return paymentInfo;
    }
}
