package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
//@DiscriminatorValue("1")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "email"}))
@NamedQuery(
        name = Constants.Q_RIDER_BY_EMAIL,
        query = "SELECT r FROM Rider r WHERE r.email = :email"
)
@NamedQuery(
        name= Constants.Q_INACTIVE_RIDER,
        query = "SELECT r FROM Rider r WHERE r.id NOT IN (" +
                    "SELECT r2.id FROM Rider r2 JOIN r2.trips t JOIN t.tripInfo i " +
                    " WHERE :start < i.completed AND i.completed < :end" +
                ")"
)
public class Rider extends PlatformUser implements IRider {
    static Rider fromIRider(IRider rider) {
        return (Rider) rider;
    }

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
