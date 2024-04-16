package dst.ass2.service.api.auth;

public interface IAuthenticationService {

    /**
     * Attempts to authenticate the user with the given unique email address and the given password in plain text, by
     * checking the data against the records in the database. If the credentials are successfully authenticated, the
     * service generates a new authentication token which is stored (with the users email address) in-memory and then
     * returned.
     *
     * @param email    the user email
     * @param password the password
     * @return a new authentication token
     * @throws NoSuchUserException     if the given user was not found
     * @throws AuthenticationException if the credentials could not be authenticated
     */
    String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException;

    /**
     * Changes the password of the given user in the database. Also updates the in-memory cache in a thread-safe way.
     *
     * @param email       the user email
     * @param newPassword the new password in plain text.
     * @throws NoSuchUserException if the given user was not found
     */
    void changePassword(String email, String newPassword) throws NoSuchUserException;

    /**
     * Returns the user that is associated with this token. Returns null if the token does not exist.
     *
     * @param token an authentication token previously created via {@link #authenticate(String, String)}
     * @return the user's email address or null
     */
    String getUser(String token);

    /**
     * Checks whether the given token is valid (i.e., was issued by this service and has not been invalidated).
     *
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean isValid(String token);

    /**
     * Invalidates the given token, i.e., removes it from the cache. Returns false if the token did not exist.
     *
     * @param token the token to invalidate
     * @return true if the token existed and was successfully invalidated, false otherwise
     */
    boolean invalidate(String token);

}
