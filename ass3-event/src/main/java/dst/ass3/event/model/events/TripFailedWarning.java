package dst.ass3.event.model.events;

import dst.ass3.event.model.domain.Region;

/**
 * Indicates that matching a trip has probably failed.
 */
public class TripFailedWarning extends Warning {

    private static final long serialVersionUID = -9120187311385112769L;

    private long eventId;

    public TripFailedWarning() {
        super(null);
    }

    public TripFailedWarning(long eventId, Region region) {
        super(region);
        this.eventId = eventId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "TripFailedWarning{" +
            "eventId=" + eventId +
            '}';
    }
}
