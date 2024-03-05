package dst.ass1.jpa.model;

import java.util.Date;

public interface ITripInfo {

    Long getId();

    void setId(Long id);

    Date getCompleted();

    void setCompleted(Date date);

    Double getDistance();

    void setDistance(Double distance);

    Integer getDriverRating();

    void setDriverRating(Integer driverRating);

    Integer getRiderRating();

    void setRiderRating(Integer riderRating);

    ITrip getTrip();

    void setTrip(ITrip trip);

    ITripReceipt getReceipt();

    void setReceipt(ITripReceipt receipt);

}
