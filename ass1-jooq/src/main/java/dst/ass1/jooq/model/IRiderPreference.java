package dst.ass1.jooq.model;

import java.util.Map;

public interface IRiderPreference {

    String getArea();

    void setArea(String area);

    String getVehicleClass();

    void setVehicleClass(String vehicleClass);

    Long getRiderId();

    void setRiderId(Long personId);

    Map<String, String> getPreferences();

    void setPreferences(Map<String, String> preferences);
}
