package dst.ass3.event.model.events;

import dst.ass3.event.model.domain.Region;

/**
 * Warning that indicates that a matching event has not reached the lifecycle state MATCHED within a given time frame.
 */
public class MatchingTimeoutWarning extends Warning {

    private static final long serialVersionUID = 7955599732178947649L;

    private long tripId;

    public MatchingTimeoutWarning() {
        super(null);
    }

    public MatchingTimeoutWarning(long tripId, Region region) {
        super(region);
        this.tripId = tripId;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    @Override
    public String toString() {
        return "MatchingTimeoutWarning{" +
            "tripId=" + tripId +
            '}';
    }
}
