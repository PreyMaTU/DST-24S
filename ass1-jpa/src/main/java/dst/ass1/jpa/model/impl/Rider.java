package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Rider implements IRider {
    static Rider fromIRider(IRider rider) {
        return (Rider) rider;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;
    @NotNull
    @Column(nullable = false)
    private String tel;
    private Double avgRating;
    @NotNull
    @Column(unique= true, nullable = false)
    private String email;
    @Column( length= 20 )
    private byte[] password;

    @OneToMany(mappedBy = "rider")
    Collection<Trip> trips= new ArrayList<>();

    @OneToMany
    Collection<PaymentInfo> paymentInfos= new ArrayList<>();

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

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email= email;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }

    @Override
    public void setPassword(byte[] password) {
        this.password= password;
    }

    @Override
    public Collection<ITrip> getTrips() {
        return new ArrayList<>( trips );
    }

    @Override
    public void setTrips(Collection<ITrip> trips) {
        if( trips == null ) {
            this.trips= new ArrayList<>();
            return;
        }
        this.trips= new ArrayList<>( trips.size() );
        trips.forEach( this::addTrip );
    }

    @Override
    public void addTrip(ITrip trip) {
        trips.add( Trip.fromITrip(trip) );
    }

    @Override
    public Collection<IPaymentInfo> getPaymentInfos() {
        return new ArrayList<>(paymentInfos);
    }

    @Override
    public void setPaymentInfos(Collection<IPaymentInfo> paymentInfos) {
        if( paymentInfos == null ) {
            this.paymentInfos= new ArrayList<>();
            return;
        }
        this.paymentInfos= new ArrayList<>( paymentInfos.size() );
        paymentInfos.forEach( p -> this.paymentInfos.add( PaymentInfo.fromIPaymentInfo( p ) ) );
    }
}
