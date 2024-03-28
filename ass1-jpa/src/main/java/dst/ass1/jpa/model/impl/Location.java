package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ILocation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Location implements ILocation {
    static Location fromILocation( ILocation l ) {
        if( l == null ) {
            return null;
        }
        final var location= new Location();
        location.setId(l.getId());
        location.setLocationId(l.getLocationId());
        location.setName(l.getName());
        return location;
    }

}
