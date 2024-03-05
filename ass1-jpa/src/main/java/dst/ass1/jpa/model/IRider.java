package dst.ass1.jpa.model;

import java.util.Collection;

public interface IRider extends IPlatformUser {

    String getEmail();

    void setEmail(String email);

    byte[] getPassword();

    void setPassword(byte[] password);

    Collection<ITrip> getTrips();

    void setTrips(Collection<ITrip> trips);

    void addTrip(ITrip trip);

    Collection<IPaymentInfo> getPaymentInfos();

    void setPaymentInfos(Collection<IPaymentInfo> paymentInfos);
}
