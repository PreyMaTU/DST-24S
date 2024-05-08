package dst.ass3.event.model.events;

import dst.ass3.event.model.domain.ITripEventInfo;
import dst.ass3.event.model.domain.Region;
import dst.ass3.event.model.domain.TripState;

import java.io.Serializable;

/**
 * Indicates a change in the lifecycle state of an ITripEventInfo.
 */
public class LifecycleEvent implements Serializable {

    private static final long serialVersionUID = 8665269919851487210L;

    /**
     * The id of the trip, as returned by {@link ITripEventInfo#getTripId()}.
     */
    private long tripId;

    private TripState state;
    private Region region;

    /**
     * The instant the event was recorded (unix epoch in milliseconds)
     */
    private long timestamp;

    public LifecycleEvent() {
    }

    public LifecycleEvent(ITripEventInfo eventInfo) {
        this(eventInfo.getTripId(), eventInfo.getState(), eventInfo.getRegion(), eventInfo.getTimestamp());
    }

    public LifecycleEvent(long tripId, TripState state, Region region, long timestamp) {
        this.tripId = tripId;
        this.state = state;
        this.region = region;
        this.timestamp = timestamp;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public TripState getState() {
        return state;
    }

    public void setState(TripState state) {
        this.state = state;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LifecycleEvent{" +
            "tripId=" + tripId +
            ", state=" + state +
            ", region=" + region +
            ", timestamp=" + timestamp +
            '}';
    }
}
