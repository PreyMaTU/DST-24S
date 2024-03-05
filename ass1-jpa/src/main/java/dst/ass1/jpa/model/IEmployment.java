package dst.ass1.jpa.model;

import java.util.Date;

public interface IEmployment {

    IEmploymentKey getId();

    void setId(IEmploymentKey employmentKey);

    Date getSince();

    void setSince(Date since);

    Boolean isActive();

    void setActive(Boolean active);
}
