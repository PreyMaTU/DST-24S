package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.model.ITripReceipt;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQuery(
        name= Constants.Q_AVERAGE_RATING_RIDER,
        query = "SELECT r.id, AVG(t.riderRating) AS rider_average FROM TripInfo t JOIN t.trip.rider r GROUP BY r.id ORDER BY rider_average DESC"
)
public class TripInfo implements ITripInfo {
    static TripInfo fromITripInfo(ITripInfo tripInfo) {
        return (TripInfo) tripInfo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn
    private Trip trip;

    @OneToOne(optional = false)
    @JoinColumn
    private TripReceipt tripReceipt;

    private Date completed;
    private Double distance;
    private int driverRating;
    private int riderRating;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id= id;
    }

    @Override
    public Date getCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(Date date) {
        this.completed= date;
    }

    @Override
    public Double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(Double distance) {
        this.distance= distance;
    }

    @Override
    public Integer getDriverRating() {
        return driverRating;
    }

    @Override
    public void setDriverRating(Integer driverRating) {
        this.driverRating= driverRating;
    }

    @Override
    public Integer getRiderRating() {
        return riderRating;
    }

    @Override
    public void setRiderRating(Integer riderRating) {
        this.riderRating= riderRating;
    }

    @Override
    public ITrip getTrip() {
        return trip;
    }

    @Override
    public void setTrip(ITrip trip) {
        this.trip= Trip.fromITrip( trip );
    }

    @Override
    public ITripReceipt getTripReceipt() {
        return tripReceipt;
    }

    @Override
    public void setTripReceipt(ITripReceipt tripReceipt) {
        this.tripReceipt = TripReceipt.fromITripReceipt(tripReceipt);
    }
}
