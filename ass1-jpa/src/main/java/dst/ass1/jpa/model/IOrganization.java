package dst.ass1.jpa.model;

import java.util.Collection;

public interface IOrganization {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    Collection<IOrganization> getParts();

    void setParts(Collection<IOrganization> parts);

    void addPart(IOrganization part);

    Collection<IOrganization> getPartOf();

    void setPartOf(Collection<IOrganization> partOf);

    void addPartOf(IOrganization partOf);

    Collection<IEmployment> getEmployments();

    void setEmployments(Collection<IEmployment> employments);

    void addEmployment(IEmployment employment);

    Collection<IVehicle> getVehicles();

    void setVehicles(Collection<IVehicle> vehicles);

    void addVehicle(IVehicle vehicle);
}
