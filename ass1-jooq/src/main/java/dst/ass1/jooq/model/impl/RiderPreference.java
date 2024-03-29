package dst.ass1.jooq.model.impl;

import dst.ass1.jooq.model.IRiderPreference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RiderPreference implements IRiderPreference {
    private String area;
    private String vehicleClass;
    private Long riderId;
    private Map<String, String> preferences= new HashMap<>();
    @Override
    public String getArea() {
        return area;
    }

    @Override
    public void setArea(String area) {
        this.area= area;
    }

    @Override
    public String getVehicleClass() {
        return vehicleClass;
    }

    @Override
    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass= vehicleClass;
    }

    @Override
    public Long getRiderId() {
        return riderId;
    }

    @Override
    public void setRiderId(Long personId) {
        this.riderId= personId;
    }

    @Override
    public Map<String, String> getPreferences() {
        return preferences;
    }

    @Override
    public void setPreferences(Map<String, String> preferences) {
        this.preferences= preferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RiderPreference)) return false;
        RiderPreference that = (RiderPreference) o;
        return Objects.equals(area, that.area) && Objects.equals(vehicleClass, that.vehicleClass) && Objects.equals(riderId, that.riderId) && Objects.equals(preferences, that.preferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(area, vehicleClass, riderId, preferences);
    }

    @Override
    public String toString() {
        return "RiderPreference{" +
                "area='" + area + '\'' +
                ", vehicleClass='" + vehicleClass + '\'' +
                ", riderId=" + riderId +
                ", preferences=" + preferences +
                '}';
    }
}
