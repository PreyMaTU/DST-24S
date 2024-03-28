package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPlatformUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="platform_user_type", discriminatorType = DiscriminatorType.INTEGER)
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "email"}))
public class PlatformUser implements IPlatformUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @NotNull
    @Column(nullable = false)
    protected String name;
    @NotNull
    @Column(nullable = false)
    protected String tel;
    protected Double avgRating;

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
