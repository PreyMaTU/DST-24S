package dst.ass2.ioc.tests.lock;

import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.lock.Lock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;


public class LockingTest {

    private static final Logger LOG = LoggerFactory.getLogger(LockingTest.class);

    @Rule
    public ThreadPoolResource executor = new ThreadPoolResource(2);

    /**
     * This class simulates the use of a shared resource (the semaphore). If the resource is already acquired
     * by some other thread, then a {@link ConcurrentModificationException} is thrown. We use this behavior to test
     * various scenarios (whether mutex access is guaranteed when the @Lock value refers to the same lock).
     */
    @Component
    public static class SimpleLockManaged {

        public Semaphore semaphore;

        public SimpleLockManaged(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Lock("my_lock")
        public void useResource() {
            useResourceImpl();
        }

        @Lock("my_other_lock")
        public void useResourceSomeMore() {
            useResourceImpl();
        }

        private void useResourceImpl() {
            LOG.info("{} trying to acquire semaphore", this);
            boolean acquired = semaphore.tryAcquire();

            if (!acquired) {
                LOG.info("{} failed to acquire semaphore", this);
                throw new ConcurrentModificationException("Semaphore was acquired concurrently");
            }

            // hold the permit for a few ms
            try {
                LOG.info("{} holding permit for 500ms", this);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                LOG.info("{} releasing semaphore", this);
                semaphore.release();
            }
        }
    }

    @Test
    public void mutexTest_onSameClassAndObject_behavesCorrectly() throws Exception {
        Semaphore semaphore = new Semaphore(1);

        SimpleLockManaged lockManaged = new SimpleLockManaged(semaphore);

        Future<?> r1 = executor.submit(lockManaged::useResource);
        Future<?> r2 = executor.submit(lockManaged::useResource);

        try {
            r1.get();
            r2.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ConcurrentModificationException) {
                throw new AssertionError("Access to shared resource was not mutually exclusive as required", e);
            } else {
                throw e;
            }
        }
    }

    @Test
    public void mutexTest_onSameClassButDifferentObject_behavesCorrectly() throws Exception {
        Semaphore semaphore = new Semaphore(1);

        SimpleLockManaged lockManaged1 = new SimpleLockManaged(semaphore);
        SimpleLockManaged lockManaged2 = new SimpleLockManaged(semaphore);

        Future<?> r1 = executor.submit(lockManaged1::useResource);
        Future<?> r2 = executor.submit(lockManaged2::useResource);

        try {
            r1.get();
            r2.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ConcurrentModificationException) {
                throw new AssertionError("Access to shared resource was not mutually exclusive as required", e);
            } else {
                throw e;
            }
        }
    }

    @Test
    public void nonMutexTest_onSameClassAndObject_behavesCorrectly() throws Exception {
        Semaphore semaphore = new Semaphore(1);

        SimpleLockManaged lockManaged = new SimpleLockManaged(semaphore);

        Future<?> r1 = executor.submit(lockManaged::useResource);
        Future<?> r2 = executor.submit(lockManaged::useResourceSomeMore);

        try {
            r1.get();
            r2.get();
            throw new AssertionError("Different locks should allow concurrent access to resources");
        } catch (ExecutionException e) {
            if (!(e.getCause() instanceof ConcurrentModificationException)) {
                throw e;
            }
            // we expected a concurrent modification exception here
        }

    }

    @Test
    public void nonMutexTest_onSameClassButDifferentObject_behavesCorrectly() throws Exception {
        Semaphore semaphore = new Semaphore(1);

        SimpleLockManaged lockManaged1 = new SimpleLockManaged(semaphore);
        SimpleLockManaged lockManaged2 = new SimpleLockManaged(semaphore);

        Future<?> r1 = executor.submit(lockManaged1::useResource);
        Future<?> r2 = executor.submit(lockManaged2::useResourceSomeMore);

        try {
            r1.get();
            r2.get();
            throw new AssertionError("Different locks should allow concurrent access to resources");
        } catch (ExecutionException e) {
            if (!(e.getCause() instanceof ConcurrentModificationException)) {
                throw e;
            }
            // we expected a concurrent modification exception here
        }

    }

    @Component
    public static class AnotherSimpleLockManaged {

        private Semaphore semaphore;

        public AnotherSimpleLockManaged(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Lock("my_lock")
        protected void useResourceAgain() {
            LOG.info("{} trying to acquire semaphore", this);
            boolean acquired = semaphore.tryAcquire();

            if (!acquired) {
                LOG.info("{} failed to acquire semaphore", this);
                throw new ConcurrentModificationException("Semaphore was acquired concurrently");
            }

            // hold the permit for a few ms
            try {
                LOG.info("{} holding permit for 500ms", this);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                LOG.info("{} releasing semaphore", this);
                semaphore.release();
            }
        }
    }

    @Test
    public void mutexTest_onDifferentClass_behavesCorrectly() throws Exception {
        Semaphore semaphore = new Semaphore(1);

        SimpleLockManaged lockManaged1 = new SimpleLockManaged(semaphore);
        AnotherSimpleLockManaged lockManaged2 = new AnotherSimpleLockManaged(semaphore);

        Future<?> r1 = executor.submit(lockManaged1::useResource);
        Future<?> r2 = executor.submit(lockManaged2::useResourceAgain);

        try {
            r1.get();
            r2.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ConcurrentModificationException) {
                throw new AssertionError("Access to shared resource was not mutually exclusive as required", e);
            } else {
                throw e;
            }
        }
    }

    /**
     * This class is annotated with @Lock, but does not have a @Component annotation, so it should not be instrumented.
     */
    public static class NotLockManaged {

        public Semaphore semaphore;

        public NotLockManaged(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Lock("my_lock_0")
        public void useResource() {
            LOG.info("{} trying to acquire semaphore", this);
            boolean acquired = semaphore.tryAcquire();

            if (!acquired) {
                LOG.info("{} failed to acquire semaphore", this);
                throw new ConcurrentModificationException("Semaphore was acquired concurrently");
            }

            // hold the permit for a few ms
            try {
                LOG.info("{} holding permit for 500ms", this);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                LOG.info("{} releasing semaphore", this);
                semaphore.release();
            }
        }
    }

    @Test
    public void testOnNonComponent_shouldNotBeInstrumented() throws Exception {
        Semaphore semaphore = new Semaphore(1);

        NotLockManaged notManaged = new NotLockManaged(semaphore);

        Future<?> r1 = executor.submit(notManaged::useResource);
        Future<?> r2 = executor.submit(notManaged::useResource);

        try {
            r1.get();
            r2.get();
            throw new AssertionError("Only classes with the @Component annotation should be instrumented");
        } catch (ExecutionException e) {
            if (!(e.getCause() instanceof ConcurrentModificationException)) {
                throw e;
            }
            // we expected a concurrent modification exception here
        }
    }

    @Component
    public static class LockManagedWithParameter {

        @Lock("my_lock_1")
        protected void useResource(Semaphore semaphore) {
            LOG.info("{} trying to acquire semaphore", this);
            boolean acquired = semaphore.tryAcquire();

            if (!acquired) {
                LOG.info("{} failed to acquire semaphore", this);
                throw new ConcurrentModificationException("Semaphore was acquired concurrently");
            }

            // hold the permit for a few ms
            try {
                LOG.info("{} holding permit for 500ms", this);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                LOG.info("{} releasing semaphore", this);
                semaphore.release();
            }
        }
    }

    @Test
    public void mutexTest_withParameter_behavesCorrectly() throws Exception {
        Semaphore semaphore = new Semaphore(1);

        LockManagedWithParameter lockManaged = new LockManagedWithParameter();

        Future<?> r1 = executor.submit(() -> lockManaged.useResource(semaphore));
        Future<?> r2 = executor.submit(() -> lockManaged.useResource(semaphore));

        try {
            r1.get();
            r2.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ConcurrentModificationException) {
                throw new AssertionError("Access to shared resource was not mutually exclusive as required", e);
            } else {
                throw e;
            }
        }
    }


    @Component
    public static class LockManagedWithReturnValue {

        public Semaphore semaphore;

        public LockManagedWithReturnValue(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Lock("my_lock")
        public Integer useResource() {
            useResourceImpl();
            return 42;
        }

        private void useResourceImpl() {
            LOG.info("{} trying to acquire semaphore", this);
            boolean acquired = semaphore.tryAcquire();

            if (!acquired) {
                LOG.info("{} failed to acquire semaphore", this);
                throw new ConcurrentModificationException("Semaphore was acquired concurrently");
            }

            // hold the permit for a few ms
            try {
                LOG.info("{} holding permit for 500ms", this);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                LOG.info("{} releasing semaphore", this);
                semaphore.release();
            }
        }
    }

    @Test
    public void mutexTest_withReturnValue_behavesCorrectly() throws Exception {
        Semaphore semaphore = new Semaphore(1);

        LockManagedWithReturnValue lockManaged = new LockManagedWithReturnValue(semaphore);

        Future<Integer> r1 = executor.submit(lockManaged::useResource);
        Future<Integer> r2 = executor.submit(lockManaged::useResource);

        try {
            Integer i1 = r1.get();
            Integer i2 = r2.get();

            Assert.assertEquals(Integer.valueOf(42), i1);
            Assert.assertEquals(Integer.valueOf(42), i2);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ConcurrentModificationException) {
                throw new AssertionError("Access to shared resource was not mutually exclusive as required", e);
            } else {
                throw e;
            }
        }
    }


    @Component
    public static class LockManagedWithParamAndReturnValue {

        private Integer i = 0;

        @Lock("my_lock_2")
        public Integer checkAndIncrement(Integer test) throws RuntimeException {
            System.out.println(Thread.currentThread().getName() + " getting value " + i);
            int x = i;

            if (i < test) {
                // this condition will always be true in the tests, just to add some logic
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                x = x + 1;
                i = x;
            }

            return i;
        }

    }

    /**
     * This test verifies that the instrumentation also works when the function has input parameters and return values.
     */
    @Test
    public void mutexTest_withParamAndReturnValue_behavesCorrectly() throws Exception {
        LockManagedWithParamAndReturnValue managed = new LockManagedWithParamAndReturnValue();

        Future<Integer> r1 = executor.submit(() -> managed.checkAndIncrement(10));
        Future<Integer> r2 = executor.submit(() -> managed.checkAndIncrement(10));

        try {
            Integer i1 = r1.get();
            Integer i2 = r2.get();

            Assert.assertNotEquals("Result of concurrent increment is equal, indicating a race condition", i1, i2);
            if (i1 < i2) {
                Assert.assertEquals(Integer.valueOf(1), i1);
                Assert.assertEquals(Integer.valueOf(2), i2);
            } else {
                Assert.assertEquals(Integer.valueOf(2), i1);
                Assert.assertEquals(Integer.valueOf(1), i2);
            }
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ConcurrentModificationException) {
                throw new AssertionError("Access to shared resource was not mutually exclusive as required", e);
            } else {
                throw e;
            }
        }
    }

}
