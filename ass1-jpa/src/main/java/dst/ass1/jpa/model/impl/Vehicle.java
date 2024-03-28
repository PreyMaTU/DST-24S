package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IVehicle;

import javax.persistence.*;

@Entity
public class Vehicle implements IVehicle {
    static Vehicle fromIVehicle(IVehicle v) {
        if( v == null ) {
            return null;
        }
        final var vehicle= new Vehicle();
        vehicle.setId(v.getId());
        vehicle.setLicense(v.getLicense());
        vehicle.setColor(v.getColor());
        vehicle.setType(v.getType());
        return vehicle;
    }
}
