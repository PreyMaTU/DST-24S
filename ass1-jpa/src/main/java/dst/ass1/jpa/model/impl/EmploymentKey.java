package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmploymentKey;
import dst.ass1.jpa.model.IOrganization;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class EmploymentKey implements IEmploymentKey, Serializable {
    static EmploymentKey fromIEmploymentKey(IEmploymentKey employmentKey) {
        return (EmploymentKey) employmentKey;
    }

    @ManyToOne
    @JoinColumn
    private Driver driver;

    @ManyToOne
    @JoinColumn
    private Organization organization;

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver= Driver.fromIDriver( driver );
    }

    @Override
    public IOrganization getOrganization() {
        return organization;
    }

    @Override
    public void setOrganization(IOrganization organization) {
        this.organization= Organization.fromIOrganization( organization );
    }
}
