package dst.ass3.messaging;

import java.util.Objects;

public class GeoPoint {


    private Double longitude;
    private Double latitude;

    public GeoPoint() {
    }

    public GeoPoint(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoPoint geoPoint = (GeoPoint) o;
        return Objects.equals(getLongitude(), geoPoint.getLongitude()) &&
            Objects.equals(getLatitude(), geoPoint.getLatitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLongitude(), getLatitude());
    }


}
