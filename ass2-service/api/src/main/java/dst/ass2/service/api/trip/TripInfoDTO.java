package dst.ass2.service.api.trip;

import java.util.Date;

public class TripInfoDTO {

    private Double distance;
    private Date completed;
    private MoneyDTO fare;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }


    public MoneyDTO getFare() {
        return fare;
    }

    public void setFare(MoneyDTO fare) {
        this.fare = fare;
    }
}
