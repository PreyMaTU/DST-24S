package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmploymentKey;
import dst.ass1.jpa.model.IOrganization;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmploymentKey)) return false;
        EmploymentKey that = (EmploymentKey) o;
        return Objects.equals(driver, that.driver) && Objects.equals(organization, that.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driver, organization);
    }
}
