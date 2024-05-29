package dst.ass3.event;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EventProcessingTestBase.
 */
public class EventProcessingTestBase extends Ass3EventTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(EventProcessingTestBase.class);

    @Rule
    public Timeout timeout = new Timeout(15, TimeUnit.SECONDS);

    private IEventProcessingEnvironment epe;

    protected StaticQueueSink<LifecycleEvent> lifecycleEvents;
    protected StaticQueueSink<MatchingDuration> matchingDurations;
    protected StaticQueueSink<AverageMatchingDuration> averageMatchingDurations;
    protected StaticQueueSink<MatchingTimeoutWarning> matchingTimeoutWarnings;
    protected StaticQueueSink<TripFailedWarning> tripFailedWarnings;
    protected StaticQueueSink<Alert> alerts;

    @Before
    public void setUpEnvironment() throws Exception {
        epe = EventProcessingFactory.createEventProcessingEnvironment();

        assertNotNull("#createEventProcessingEnvironment() not implemented", epe);

        lifecycleEvents = new StaticQueueSink<>("lifecycleEvents");
        matchingDurations = new StaticQueueSink<>("matchingDurations");
        averageMatchingDurations = new StaticQueueSink<>("averageMatchingDurations");
        matchingTimeoutWarnings = new StaticQueueSink<>("matchingTimeoutWarnings");
        tripFailedWarnings = new StaticQueueSink<>("tripFailedWarnings");
        alerts = new StaticQueueSink<>("alerts");

        epe.setLifecycleEventStreamSink(lifecycleEvents);
        epe.setMatchingDurationStreamSink(matchingDurations);
        epe.setAverageMatchingDurationStreamSink(averageMatchingDurations);
        epe.setMatchingTimeoutWarningStreamSink(matchingTimeoutWarnings);
        epe.setTripFailedWarningStreamSink(tripFailedWarnings);
        epe.setAlertStreamSink(alerts);
        epe.setMatchingDurationTimeout(Time.seconds(15));
    }

    public JobExecutionResult initAndExecute() throws Exception {
        return initAndExecute(null);
    }

    public JobExecutionResult initAndExecute(Consumer<IEventProcessingEnvironment> initializer) throws Exception {
        try {
            if (initializer != null) {
                initializer.accept(epe);
            }
            LOG.info("Initializing StreamExecutionEnvironment with {}", epe);
            epe.initialize(flink);
        } catch (Exception e) {
            LOG.error("Error while initializing StreamExecutionEnvironment", e);
            throw e;
        }

        try {
            LOG.info("Executing flink {}", flink);
            return flink.execute();
        } catch (Exception e) {
            LOG.error("Error while executing flink", e);
            throw e;
        }
    }

    public Future<JobExecutionResult> initAndExecuteAsync(Consumer<IEventProcessingEnvironment> initializer) {
        return executor.submit(() -> initAndExecute(initializer));
    }

    public Future<JobExecutionResult> initAndExecuteAsync() {
        return executor.submit(() -> initAndExecute());
    }

    @After
    public void tearDownCollectors() throws Exception {
        StaticQueueSink.clearAll();
    }

}
