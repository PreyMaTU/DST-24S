package dst.ass2.service.auth;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.IAuthenticationService;
import dst.ass2.service.api.auth.NoSuchUserException;

public interface ICachingAuthenticationService extends IAuthenticationService {

    /**
     * {@inheritDoc}
     *
     * <p>
     * Instead of checking database records directly, the method first checks the cache for existing users. If the user
     * is not in the cache, then the service checks the database for the given email address, and updates the cache if
     * necessary.
     * </p>
     */
    @Override
    String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException;

    /**
     * Loads user data from the database into memory.
     */
    void loadData();

    /**
     * Clears the data cached from the database.
     */
    void clearCache();
}
