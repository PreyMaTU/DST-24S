package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;
import dst.ass1.jpa.util.Constants;

import javax.persistence.NamedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@NamedQuery(
        name = Constants.Q_TRIP_BY_STATE,
        query = "SELECT t FROM Trip t WHERE t.state = :state"
)
public class Trip implements ITrip {
    static Trip fromITrip( ITrip trip ) {
        return (Trip) trip;
    }

    private Long id;

    private Location pickup;
    private Location destination;
    private Collection<Location> stops= new ArrayList<>();

    private TripInfo tripInfo;
    private Match match;
    private Rider rider;

    private Date created;
    private Date updated;
    private TripState state;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id= id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created= created;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated= updated;
    }

    @Override
    public TripState getState() {
        return state;
    }

    @Override
    public void setState(TripState state) {
        this.state= state;
    }

    @Override
    public Location getPickup() {
        return pickup;
    }

    @Override
    public void setPickup(ILocation pickup) {
        this.pickup= Location.fromILocation(pickup);
    }

    @Override
    public Location getDestination() {
        return destination;
    }

    @Override
    public void setDestination(ILocation destination) {
        this.destination= Location.fromILocation( destination );
    }

    @Override
    public Collection<ILocation> getStops() {
        return new ArrayList<>(stops);
    }

    @Override
    public void setStops(Collection<ILocation> stops) {
        if( stops == null ) {
            this.stops= new ArrayList<>();
            return;
        }
        this.stops= new ArrayList<>( stops.size() );
        stops.forEach( this::addStop );
    }

    @Override
    public void addStop(ILocation stop) {
        stops.add( Location.fromILocation(stop) );
    }

    @Override
    public TripInfo getTripInfo() {
        return tripInfo;
    }

    @Override
    public void setTripInfo(ITripInfo tripInfo) {
        this.tripInfo= TripInfo.fromITripInfo(tripInfo);
    }

    @Override
    public Match getMatch() {
        return match;
    }

    @Override
    public void setMatch(IMatch match) {
        this.match= Match.fromIMatch(match);
    }

    @Override
    public Rider getRider() {
        return rider;
    }

    @Override
    public void setRider(IRider rider) {
        this.rider= Rider.fromIRider(rider);
    }
}
