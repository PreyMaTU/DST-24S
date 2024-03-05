package dst.ass1.jpa.model;

import java.util.Collection;
import java.util.Date;

public interface ITrip {

    Long getId();

    void setId(Long id);

    Date getCreated();

    void setCreated(Date created);

    Date getUpdated();

    void setUpdated(Date updated);

    TripState getState();

    void setState(TripState state);

    ILocation getPickup();

    void setPickup(ILocation pickup);

    ILocation getDestination();

    void setDestination(ILocation destination);

    Collection<ILocation> getStops();

    void setStops(Collection<ILocation> stops);

    void addStop(ILocation stop);

    ITripInfo getTripInfo();

    void setTripInfo(ITripInfo tripInfo);

    IMatch getMatch();

    void setMatch(IMatch match);

    IRider getRider();

    void setRider(IRider rider);
}
