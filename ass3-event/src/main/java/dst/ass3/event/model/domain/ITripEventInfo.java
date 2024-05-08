package dst.ass3.event.model.domain;

public interface ITripEventInfo {

    Long getTripId();

    Long getTimestamp();

    TripState getState();

    Region getRegion();
}
