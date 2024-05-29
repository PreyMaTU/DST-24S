package dst.ass3.event.dto;

import dst.ass3.event.model.domain.ITripEventInfo;
import dst.ass3.event.model.domain.Region;
import dst.ass3.event.model.domain.TripState;

import java.io.Serializable;

public class TripEventInfoDTO implements Serializable, ITripEventInfo {

    private static final long serialVersionUID = 4134104076758220138L;

    private Long tripId;
    private Long timestamp;
    private TripState state;
    private Region region;

    public TripEventInfoDTO(Long tripId, Long timestamp, TripState state, Region region) {
        this.tripId = tripId;
        this.timestamp = timestamp;
        this.state = state;
        this.region = region;
    }

    @Override
    public Long getTripId() {
        return tripId;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public TripState getState() {
        return state;
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return "TripEventInfoDTO{" +
            "tripId=" + tripId +
            ", timestamp=" + timestamp +
            ", state=" + state +
            ", region=" + region +
            '}';
    }
}
