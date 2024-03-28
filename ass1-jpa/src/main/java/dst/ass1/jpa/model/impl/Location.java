package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ILocation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Location implements ILocation {
    static Location fromILocation( ILocation location ) {
        return (Location) location;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private Long locationId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getLocationId() {
        return locationId;
    }

    @Override
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
