package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IMoney;
import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.model.ITripReceipt;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;

@Entity
public class TripReceipt implements ITripReceipt {
    static TripReceipt fromITripReceipt( ITripReceipt r ) {
        if( r == null ) {
            return null;
        }
        final var receipt= new TripReceipt();
        receipt.setId(r.getId());
        receipt.setPaymentInfo(r.getPaymentInfo());
        receipt.setTripInfo(r.getTripInfo());
        receipt.setTotal(r.getTotal());
        receipt.setTip(r.getTip());
        receipt.setPaid(r.isPaid());
        return receipt;
    }
}
