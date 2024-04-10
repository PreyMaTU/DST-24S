package dst.ass2.service.api.trip;

import java.io.Serializable;

public class MatchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long driverId;
    private Long vehicleId;

    private MoneyDTO fare;

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }


    public MoneyDTO getFare() {
        return fare;
    }

    public void setFare(MoneyDTO fare) {
        this.fare = fare;
    }
}
