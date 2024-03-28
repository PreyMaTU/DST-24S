package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Trip implements ITrip {
    static Trip fromITrip( ITrip t ) {
        if( t == null ) {
            return null;
        }
        final var trip= new Trip();
        trip.setId(t.getId());
        trip.setCreated(t.getCreated());
        trip.setUpdated(t.getUpdated());
        trip.setState(t.getState());
        trip.setPickup(t.getPickup());
        trip.setDestination(t.getDestination());
        trip.setStops(t.getStops());
        trip.setTripInfo(t.getTripInfo());
        trip.setMatch(t.getMatch());
        trip.setRider(t.getRider());
        return trip;
    }

}
