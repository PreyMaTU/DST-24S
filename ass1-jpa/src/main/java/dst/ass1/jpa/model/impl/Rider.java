package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Rider implements IRider {
    static Rider fromIRider(IRider r) {
        if( r == null ) {
            return null;
        }
        final var rider= new Rider();
        rider.setId(r.getId());
        rider.setName(r.getName());
        rider.setTel(r.getTel());
        rider.setEmail(r.getEmail());
        rider.setAvgRating(r.getAvgRating());
        rider.setPassword(r.getPassword());
        rider.setTrips(r.getTrips());
        rider.setPaymentInfos(r.getPaymentInfos());
        return rider;
    }
}
