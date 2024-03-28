package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
public class Driver implements IDriver {
    static Driver fromIDriver(IDriver d) {
        if( d == null ) {
            return null;
        }
        final var driver= new Driver();
        driver.setId(d.getId());
        driver.setVehicle(d.getVehicle());
        driver.setEmployments(d.getEmployments());
        driver.setName(d.getName());
        driver.setTel(d.getTel());
        driver.setAvgRating(d.getAvgRating());
        return driver;
    }

}
