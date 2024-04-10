package dst.ass2.ioc.tests.lock;

import org.junit.rules.ExternalResource;

import java.util.concurrent.*;

/**
 * Simple JUnit Rule wrapper of a fixed thread pool {@link ExecutorService} that shuts down after each test execution.
 */
public class ThreadPoolResource extends ExternalResource {

    private ExecutorService executor;
    private int nThreads;

    public ThreadPoolResource(int nThreads) {
        this.nThreads = nThreads;
    }

    @Override
    protected void before() {
        executor = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    protected void after() {
        shutdownAndAwaitTermination(executor);
    }

    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        System.out.println("shutting down pool");
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            System.out.println("awaiting pool termination");
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
