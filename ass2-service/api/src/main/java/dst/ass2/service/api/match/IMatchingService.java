package dst.ass2.service.api.match;

import dst.ass2.service.api.trip.InvalidTripException;
import dst.ass2.service.api.trip.MoneyDTO;
import dst.ass2.service.api.trip.TripDTO;

/**
 * An implementation of this interface is provided to you by the application.
 */
public interface IMatchingService {

    /**
     * The passed trip needs at least the following properties set: riderId, pickup, destination.
     *
     * @param trip the trip the MatchingService will calculate a fare for
     * @return the proposed fare for the given trip
     * @throws InvalidTripException in case the route cannot be calculated
     */
    MoneyDTO calculateFare(TripDTO trip) throws InvalidTripException;

    /**
     * Puts the trip into the queue for driver matching.
     * The trip with the specified ID needs at least the following properties set:
     * riderId, pickup, destination and fare
     *
     * @param tripId
     */
    void queueTripForMatching(Long tripId);
}
