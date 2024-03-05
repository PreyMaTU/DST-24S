package dst.ass1.jpa.model;

public interface IVehicle {

    Long getId();

    void setId(Long id);

    String getLicense();

    void setLicense(String license);

    String getColor();

    void setColor(String color);

    String getType();

    void setType(String type);
}
