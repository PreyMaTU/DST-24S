package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.IVehicle;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Organization implements IOrganization {
    static Organization fromIOrganization(IOrganization organization ) {
        return (Organization) organization;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = Constants.J_ORGANIZATION_PARTS,
            joinColumns = { @JoinColumn(name= Constants.I_ORGANIZATION_PARTS) },
            inverseJoinColumns = { @JoinColumn(name = Constants.I_ORGANIZATION_PART_OF) }
    )
    Collection<Organization> parts;

    @ManyToMany( mappedBy = "parts" )
    Collection<Organization> partOf;

    @ManyToMany
    Collection<Vehicle> vehicles= new ArrayList<>();

    @OneToMany(mappedBy = "id.organization")
    Collection<Employment> employments= new ArrayList<>();

    private String name;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id= id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name= name;
    }

    @Override
    public Collection<IOrganization> getParts() {
        return new ArrayList<>( parts );
    }

    @Override
    public void setParts(Collection<IOrganization> parts) {
        if( parts == null ) {
            this.parts= new ArrayList<>();
            return;
        }
        this.parts= new ArrayList<>( parts.size() );
        parts.forEach( this::addPart );
    }

    @Override
    public void addPart(IOrganization part) {
        parts.add( Organization.fromIOrganization(part) );
    }

    @Override
    public Collection<IOrganization> getPartOf() {
        return new ArrayList<>( partOf );
    }

    @Override
    public void setPartOf(Collection<IOrganization> partOf) {
        if( partOf == null ) {
            this.partOf= new ArrayList<>();
            return;
        }
        this.partOf= new ArrayList<>( partOf.size() );
        partOf.forEach( this::addPartOf );
    }

    @Override
    public void addPartOf(IOrganization partOf) {
        this.partOf.add( Organization.fromIOrganization(partOf) );
    }

    @Override
    public Collection<IEmployment> getEmployments() {
        return new ArrayList<>( employments );
    }

    @Override
    public void setEmployments(Collection<IEmployment> employments) {
        if( employments == null ) {
            this.employments= new ArrayList<>();
            return;
        }
        this.employments= new ArrayList<>( employments.size() );
        employments.forEach( this::addEmployment );
    }

    @Override
    public void addEmployment(IEmployment employment) {
        employments.add( Employment.fromIEmployment(employment) );
    }

    @Override
    public Collection<IVehicle> getVehicles() {
        return new ArrayList<>( vehicles );
    }

    @Override
    public void setVehicles(Collection<IVehicle> vehicles) {
        if( vehicles == null ) {
            this.vehicles= new ArrayList<>();
            return;
        }
        this.vehicles= new ArrayList<>( vehicles.size() );
        vehicles.forEach( this::addVehicle );
    }

    @Override
    public void addVehicle(IVehicle vehicle) {
        vehicles.add( Vehicle.fromIVehicle(vehicle) );
    }
}
