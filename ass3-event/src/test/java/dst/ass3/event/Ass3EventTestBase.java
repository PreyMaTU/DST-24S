package dst.ass3.event;

import org.apache.flink.shaded.guava30.com.google.common.util.concurrent.MoreExecutors;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ass3EventTestBase.
 */
public abstract class Ass3EventTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(Ass3EventTestBase.class);

    protected EventPublisher publisher;
    protected StreamExecutionEnvironment flink;
    protected ExecutorService executor;

    private static EventPublisher previousPublisher;

    @Before
    public void setUpResources() throws Exception {
        executor = Executors.newCachedThreadPool();

        if (previousPublisher != null) {
            previousPublisher.close();
        }

        publisher = createEventPublisher();
        previousPublisher = publisher;
        publisher.start();

        flink = createStreamExecutionEnvironment();
    }

    @After
    public void tearDownResources() throws Exception {
        publisher.close();
        MoreExecutors.shutdownAndAwaitTermination(executor, 5, TimeUnit.SECONDS);
        previousPublisher = null;
    }

    protected EventPublisher createEventPublisher() {
        return new EventPublisher(Constants.EVENT_PUBLISHER_PORT);
    }

    protected StreamExecutionEnvironment createStreamExecutionEnvironment() {
        return StreamExecutionEnvironment.createLocalEnvironment(1);
    }

    protected static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    protected static long now() {
        return System.currentTimeMillis();
    }

}
