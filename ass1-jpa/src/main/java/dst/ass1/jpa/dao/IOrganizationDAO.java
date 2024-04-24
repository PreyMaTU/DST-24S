package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IOrganization;

public interface IOrganizationDAO extends GenericDAO<IOrganization> {
    default String getEntityName() { return "Organization"; }


}
