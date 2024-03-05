package dst.ass1.jpa.model;

import java.util.Date;

public interface IMatch {

    Long getId();

    void setId(Long id);

    Date getDate();

    void setDate(Date date);

    IMoney getFare();

    void setFare(IMoney money);

    ITrip getTrip();

    void setTrip(ITrip trip);

    IVehicle getVehicle();

    void setVehicle(IVehicle vehicle);

    IDriver getDriver();

    void setDriver(IDriver driver);
}
