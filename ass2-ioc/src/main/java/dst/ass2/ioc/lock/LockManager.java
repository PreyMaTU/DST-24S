package dst.ass2.ioc.lock;


import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private static LockManager instance;
    private final HashMap<String, Lock> locks= new HashMap<>();

    public synchronized static LockManager getInstance() {
        if( instance == null ) {
            instance = new LockManager();
        }

        return instance;
    }

    public synchronized Lock getLock( String name ) {
        return locks.computeIfAbsent(name, k -> new ReentrantLock() );
    }
}
