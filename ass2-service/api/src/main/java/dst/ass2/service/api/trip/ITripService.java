package dst.ass2.service.api.trip;

public interface ITripService {

    /**
     * Creates and persists a Trip, sets the state to CREATED and calculates an initial fare estimation for
     * the route 'pickupId - destinationId'
     *
     * @param riderId       the id of the rider, who is planning a trip
     * @param pickupId      the id of the pickupId location
     * @param destinationId the id of the destinationId location
     * @return a TripDTO corresponding to the persisted Trip and includes the fare (null if route is invalid)
     * @throws EntityNotFoundException if the rider or one of the locations doesn't exist
     */
    TripDTO create(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException;


    /**
     * Validates and confirms the given trip (i.e., sets the state of the corresponding trip to QUEUED and
     * puts it into the queue for matching), if possible (i.e., the trip is still in CREATED state)
     *
     * @param tripId the trip to confirm
     * @throws EntityNotFoundException if the trip cannot be found
     * @throws IllegalStateException   in case the trip is not in state CREATED or the rider is null
     * @throws InvalidTripException    in case the fare couldn't be estimated
     */
    void confirm(Long tripId) throws EntityNotFoundException, IllegalStateException, InvalidTripException;


    /**
     * Creates a match for the given trip and sets the trip's state to MATCHED, if possible (i.e., if the trips is
     * QUEUED). In case something goes wrong, re-queues the trip for a new match.
     * You can assume that the locations (pickup, destination and stops) haven't been deleted and will not be deleted
     * during the execution of this method.
     *
     * @param tripId the id of the trip the match will be created for
     * @param match  the match, containing the driver, the vehicle and the fare
     * @throws EntityNotFoundException     in case one of the following doesn't exist anymore: trip, driver or
     *                                     vehicle
     * @throws DriverNotAvailableException in case the driver was assigned in the meantime to another customer
     * @throws IllegalStateException       in case the rider of the trip is null or the trip is not in QUEUED state
     */
    void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException;


    /**
     * Completes the trip (i.e., persists a TripInfo object and set the trips state to COMPLETED)
     *
     * @param tripId      the id of the trip to complete
     * @param tripInfoDTO the tripInfo to persist
     * @throws EntityNotFoundException in case the trip doesn't exist
     */
    void complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException;


    /**
     * Cancels the given trip (i.e., sets the state to cancelled)
     *
     * @param tripId the trip to cancel
     * @throws EntityNotFoundException in case the trip doesn't exist
     */
    void cancel(Long tripId) throws EntityNotFoundException;


    /**
     * Adds the location as a stop if possible (i.e., if the referenced trip is still in the CREATED state and
     * the given location is not already in the list of stops.)
     * As a side effect, the list of the passed TripDTO is modified and the fare freshly estimated.
     * In case the estimation fails, sets the fare to null.
     * You can assume that the passed TripDTO and the Trip entity have the same values
     *
     * @param trip       the trip
     * @param locationId the location
     * @return true if the stop was added, otherwise false
     * @throws EntityNotFoundException in case the location or trip doesn't exist
     * @throws IllegalStateException   in case the trip isn't longer in the CREATED state
     */
    boolean addStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException;


    /**
     * Removes the location from the stops only (i.e., if the referenced trip is still in the CREATED state).
     * As a side effect, the list of the passed TripDTO is modified and the fare freshly estimated.
     * In case the estimation fails, sets the fare to null.
     * You can assume that the passed TripDTO and the Trip entity have the same values
     *
     * @param trip       the trip
     * @param locationId the location to remove
     * @return true if the trip was removed, otherwise false (for example when the location wasn't added to the stops)
     * @throws EntityNotFoundException in case the location or trip doesn't exist
     * @throws IllegalStateException   in case the  trip isn't longer in the CREATED state
     */
    boolean removeStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException;


    /**
     * Removes the trip with the given id.
     *
     * @param tripId the id of the trip to remove
     * @throws EntityNotFoundException in case the trip doesn't exist
     */
    void delete(Long tripId) throws EntityNotFoundException;


    /**
     * Finds the trip with the given id and returns it as DTO, including the latest fare estimation.
     *
     * @param tripId the id of the trip
     * @return if found the DTO, otherwise null
     */
    TripDTO find(Long tripId);
}
