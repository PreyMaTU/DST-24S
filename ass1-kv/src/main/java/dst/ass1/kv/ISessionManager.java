package dst.ass1.kv;

public interface ISessionManager {

    /**
     * Creates a new session id and stores the userId and session timeout under the given id in the session store.
     *
     * @param userId     the user to create the session for
     * @param timeToLive the session timeout after which the session expires in seconds
     * @return the session id.
     * @throws SessionCreationFailedException if the session couldn't be created, e.g., due to a failed transaction
     */
    String createSession(Long userId, int timeToLive) throws SessionCreationFailedException;

    /**
     * Sets a key--value pair in the given session.
     *
     * @param sessionId the session to store the variable in
     * @param key       the name of the variable
     * @param value     the value
     * @throws SessionNotFoundException if the session wasn't found
     */
    void setSessionVariable(String sessionId, String key, String value) throws SessionNotFoundException;

    /**
     * Returns the value of the given session variable.
     *
     * @param sessionId the session id
     * @param key       the name of the variable
     * @return the variable value, or null if it doesn't exist
     * @throws SessionNotFoundException if the given session id has expired or doesn't exist
     */
    String getSessionVariable(String sessionId, String key) throws SessionNotFoundException;

    /**
     * Returns the user to whom the given session belongs.
     *
     * @param sessionId the session id
     * @return the user id
     * @throws SessionNotFoundException if the given session id has expired or doesn't exist
     */
    Long getUserId(String sessionId) throws SessionNotFoundException;

    /**
     * Returns the current time-to-live for the given session.
     *
     * @param sessionId the session id
     * @return the session ttl in seconds
     * @throws SessionNotFoundException if the given session id has expired or doesn't exist
     */
    int getTimeToLive(String sessionId) throws SessionNotFoundException;

    /**
     * Checks whether the given user has an open session, if so, the session id is returned (and the timeout
     * parameter ignored), otherwise a new session is created with the given timeout and the newly generated id is
     * returned.
     *
     * @param userId     the user to require the session for
     * @param timeToLive the session timeout after which the session expires in seconds
     * @return the session id.
     * @throws SessionCreationFailedException if the session couldn't be created, e.g., due to a failed transaction
     */
    String requireSession(Long userId, int timeToLive) throws SessionCreationFailedException;

    /**
     * Closes whatever underlying connection is required to maintain the session manager.
     */
    void close();
}
