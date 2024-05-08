package dst.ass3.event.model.events;

import dst.ass3.event.model.domain.Region;

import java.io.Serializable;

/**
 * Indicates the amount of time an ITripEventInfo took to get from CREATED to MATCHED.
 */
public class MatchingDuration implements Serializable {

    private static final long serialVersionUID = -6976972381929291369L;

    private long eventId;
    private Region region;
    private long duration;

    public MatchingDuration() {
    }

    public MatchingDuration(long eventId, Region region, long duration) {
        this.eventId = eventId;
        this.region = region;
        this.duration = duration;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MatchingDuration{" +
            "eventId=" + eventId +
            ", region='" + region + '\'' +
            ", duration=" + duration +
            '}';
    }
}
