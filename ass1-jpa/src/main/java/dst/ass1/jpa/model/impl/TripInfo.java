package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.ITripReceipt;
import dst.ass1.jpa.model.ITripInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TripInfo implements ITripInfo {
    static TripInfo fromITripInfo(ITripInfo i) {
        if( i == null ) {
            return null;
        }
        final var tripInfo= new TripInfo();
        tripInfo.setId(i.getId());
        tripInfo.setTrip(i.getTrip());
        tripInfo.setCompleted(i.getCompleted());
        tripInfo.setTripReceipt(i.getTripReceipt());
        tripInfo.setDistance(i.getDistance());
        tripInfo.setDriverRating(i.getDriverRating());
        tripInfo.setRiderRating(i.getRiderRating());
        return tripInfo;
    }

}
