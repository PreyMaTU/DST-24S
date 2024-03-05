package dst.ass1.jpa.model;

import java.util.Collection;

public interface IDriver extends IPlatformUser {

    Collection<IEmployment> getEmployments();

    void setEmployments(Collection<IEmployment> employments);

    void addEmployment(IEmployment employment);

    IVehicle getVehicle();

    void setVehicle(IVehicle vehicle);

}
