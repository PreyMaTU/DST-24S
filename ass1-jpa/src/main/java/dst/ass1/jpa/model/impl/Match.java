package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Match implements IMatch {

    static Match fromIMatch(IMatch m) {
        if( m == null ) {
            return null;
        }
        final var match= new Match();
        match.setId(m.getId());
        match.setTrip(m.getTrip());
        match.setDriver(m.getDriver());
        match.setVehicle(m.getVehicle());
        match.setDate(m.getDate());
        match.setFare(m.getFare());
        return match;
    }
}
