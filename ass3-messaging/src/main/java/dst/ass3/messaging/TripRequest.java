package dst.ass3.messaging;

import java.util.Objects;

public class TripRequest {

    private String id;
    private Region region;
    private GeoPoint pickup;

    public TripRequest() {
    }

    public TripRequest(String id, Region region, GeoPoint pickup) {
        this.id = id;
        this.region = region;
        this.pickup = pickup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public GeoPoint getPickup() {
        return pickup;
    }

    public void setPickup(GeoPoint pickup) {
        this.pickup = pickup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TripRequest that = (TripRequest) o;
        return Objects.equals(getId(), that.getId()) &&
            getRegion() == that.getRegion() &&
            Objects.equals(getPickup(), that.getPickup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRegion(), getPickup());
    }

    @Override
    public String toString() {
        return "TripRequest{" +
            "id='" + id + '\'' +
            ", region=" + region +
            ", pickup=" + pickup +
            '}';
    }
}
