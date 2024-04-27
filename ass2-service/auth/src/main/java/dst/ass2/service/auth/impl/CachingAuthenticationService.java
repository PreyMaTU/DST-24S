package dst.ass2.service.auth.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.model.IRider;
import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.ICachingAuthenticationService;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock ;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

@Singleton
@ManagedBean
public class CachingAuthenticationService implements ICachingAuthenticationService {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    IDAOFactory daoFactory;

    final ConcurrentHashMap<String, byte[]> passwordHashes= new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, String> validTokens= new ConcurrentHashMap<>();

    final ReadWriteLock  lock = new ReentrantReadWriteLock();
    final Lock readLock= lock.readLock();
    final Lock writeLock= lock.writeLock();

    private void write( Runnable fn ) {
        try {
            writeLock.lock();
            fn.run();

        } finally {
            writeLock.unlock();
        }
    }

    private IRider findRider( String email ) throws NoSuchUserException {
        final var user= daoFactory.createRiderDAO().findByEmail( email );
        if( user == null ) {
            throw new NoSuchUserException( String.format( "User with email '%s' not found", email ) );
        }

        return user;
    }

    private byte[] hashPassword( String password ) {
        try {
            return MessageDigest.getInstance("SHA1").digest( password.getBytes() );
        } catch( NoSuchAlgorithmException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        try {
            readLock.lock();

            // Try getting a cached password hash or read from db
            var cachedHash= passwordHashes.get( email );
            if( cachedHash == null ) {
                final var rider= findRider( email );
                cachedHash= rider.getPassword();
                passwordHashes.put( email, cachedHash );
            }

            // Check if passwords match
            final var hash= hashPassword( password );
            if( !Arrays.equals(hash, cachedHash) ) {
                throw new AuthenticationException( "Invalid password" );
            }

            // Create new token
            final var token = UUID.randomUUID().toString();
            validTokens.put(token, email);
            return token;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @PostConstruct
    public void loadData() {
        write(() -> {
            final var riders= daoFactory.createRiderDAO().findAll();
            for( final var rider : riders ) {
                passwordHashes.put(rider.getEmail(), rider.getPassword());
            }
        });
    }

    @Override
    public void clearCache() {
        write(() -> {
            passwordHashes.clear();
            validTokens.clear();
        });
    }

    @Override
    public void changePassword(String email, String newPassword) throws NoSuchUserException {
        try {
            writeLock.lock();
            final var rider= findRider( email );
            final var hash= hashPassword( newPassword );
            rider.setPassword( hash );
            passwordHashes.put(email, hash );

        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String getUser(String token) {
        return validTokens.get(token);
    }

    @Override
    public boolean isValid(String token) {
        return validTokens.containsKey(token);
    }

    @Override
    public boolean invalidate(String token) {
        return validTokens.remove( token ) != null;
    }
}
