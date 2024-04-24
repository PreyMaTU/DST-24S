package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IRider;

import java.util.Date;
import java.util.List;

public interface IRiderDAO extends GenericDAO<IRider> {
    default String getEntityName() { return "Rider"; }


    /**
     * Finds the list of riders who have no trips within the specified date range.
     * The date range is not optional.
     *
     * @param start the lower bound of the date range
     * @param end   the upper bound of the date range
     * @return a list of riders with no trips
     * @throws IllegalArgumentException in case either start or end is null.
     */
    List<IRider> findRidersWithNoTrips(Date start, Date end);

    /**
     * Returns the rider associated with the given email. Returns null if the email does not exist.
     *
     * @param email the email address
     * @return the rider or null
     */
    IRider findByEmail(String email);
}
