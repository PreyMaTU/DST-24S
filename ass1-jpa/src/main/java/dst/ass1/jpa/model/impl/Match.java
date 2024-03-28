package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Match implements IMatch {

    static Match fromIMatch(IMatch match) {
        return (Match) match;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn
    private Trip trip;

    @ManyToOne(optional = false)
    private Driver driver;

    @ManyToOne(optional = false)
    private Vehicle vehicle;

    private Date date;
    private Money fare;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id= id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date= date;
    }

    @Override
    public IMoney getFare() {
        return fare;
    }

    @Override
    public void setFare(IMoney money) {
        this.fare= Money.fromIMoney( money );
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
    public IVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle= Vehicle.fromIVehicle( vehicle );
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver= Driver.fromIDriver( driver );
    }
}
