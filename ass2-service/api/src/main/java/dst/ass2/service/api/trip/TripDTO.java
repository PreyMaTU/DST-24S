package dst.ass2.service.api.trip;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TripDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long riderId;
    private Long pickupId;
    private Long destinationId;
    private List<Long> stops;
    private MoneyDTO fare;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public Long getPickupId() {
        return pickupId;
    }

    public void setPickupId(Long pickupId) {
        this.pickupId = pickupId;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public List<Long> getStops() {
        if (stops == null) {
            stops = new LinkedList<>();
        }
        return stops;
    }

    public void setStops(List<Long> stops) {
        this.stops = stops;
    }

    public MoneyDTO getFare() {
        return fare;
    }

    public void setFare(MoneyDTO fare) {
        this.fare = fare;
    }
}
