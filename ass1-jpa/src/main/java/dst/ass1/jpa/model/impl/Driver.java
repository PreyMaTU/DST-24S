package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Driver implements IDriver {
    static Driver fromIDriver(IDriver driver) {
        return (Driver) driver;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "id.driver")
    Collection<Employment> employments= new ArrayList<>();

    @ManyToOne(optional = false)
    private Vehicle vehicle;

    @NotNull
    @Column(nullable = false)
    private String name;
    @NotNull
    @Column(nullable = false)
    private String tel;
    private Double avgRating;


    @Override
    public Collection<IEmployment> getEmployments() {
        return new ArrayList<>(employments);
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
    public IVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle= Vehicle.fromIVehicle(vehicle);
    }

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
    public String getTel() {
        return tel;
    }

    @Override
    public void setTel(String tel) {
        this.tel= tel;
    }

    @Override
    public Double getAvgRating() {
        return avgRating;
    }

    @Override
    public void setAvgRating(Double avgRating) {
        this.avgRating= avgRating;
    }
}
