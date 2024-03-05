package dst.ass1.doc;

import dst.ass1.jpa.model.ILocation;

public class MockLocation implements ILocation {

    private Long id;
    private String name;
    private Long LocationId;


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
        return LocationId;
    }

    @Override
    public void setLocationId(Long locationId) {
        LocationId = locationId;
    }
}
