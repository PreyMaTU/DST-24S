package dst.ass3.event.tests;

import dst.ass3.event.EventProcessingTestBase;
import dst.ass3.event.dto.TripEventInfoDTO;
import dst.ass3.event.model.domain.Region;
import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static dst.ass3.event.model.domain.TripState.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

public class Ass3_3_4Test extends EventProcessingTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(Ass3_3_4Test.class);

    @Test
    public void multipleTripFailures_triggerAlert() throws Exception {
        // checks that the window works correctly
        // expects TripFailedWarning stream to work correctly

        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync();

        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        Consumer<Long> causeWarning = (eventId) -> {
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, MATCHED, Region.AT_VIENNA));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, QUEUED, Region.AT_VIENNA));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, MATCHED, Region.AT_VIENNA));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, QUEUED, Region.AT_VIENNA));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, MATCHED, Region.AT_VIENNA));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, QUEUED, Region.AT_VIENNA));
        };

        // all of these events will fail
        publisher.publish(new TripEventInfoDTO(1L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(1L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(2L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(2L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(3L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(3L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(4L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(4L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(5L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(5L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(6L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(6L, now(), QUEUED, Region.AT_VIENNA));

        // warning 3 warnings
        causeWarning.accept(1L);
        causeWarning.accept(2L);
        causeWarning.accept(3L);

        LOG.info("Collecting alert for first three warnings");
        assertNotNull(alerts.take());

        LOG.info("Checking that the fourth warning does not trigger an alert");
        causeWarning.accept(4L);
        assertNull(alerts.poll(500));

        LOG.info("Checking that the fifth warning does not trigger an alert");
        causeWarning.accept(5L);
        assertNull(alerts.poll(500));

        LOG.info("Checking that the sixth warning triggered an alert");
        causeWarning.accept(6L);
        assertNotNull(alerts.take());

        publisher.dropClients();
        flinkExecution.get();
    }

    @Test
    public void matchingFailuresAndTimeouts_triggerAlert() throws Exception {
        // checks whether keying works correctly, and whether Warning streams are unioned correctly
        // expects both warning streams to work correctly

        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync(e ->
            e.setMatchingDurationTimeout(Time.seconds(3))
        );

        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        BiConsumer<Long, Region> causeWarning = (eventId, region) -> {
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, MATCHED, region));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, QUEUED, region));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, MATCHED, region));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, QUEUED, region));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, MATCHED, region));
            publisher.publish(5, t -> new TripEventInfoDTO(eventId, t, QUEUED, region));
        };

        publisher.publish(new TripEventInfoDTO(1L, now(), CREATED, Region.AT_VIENNA)); // vienna e1 will fail
        publisher.publish(new TripEventInfoDTO(1L, now(), QUEUED, Region.AT_VIENNA)); // vienna e1 will fail
        publisher.publish(new TripEventInfoDTO(2L, now(), CREATED, Region.AT_VIENNA)); // vienna e2 will fail
        publisher.publish(new TripEventInfoDTO(2L, now(), QUEUED, Region.AT_VIENNA)); // vienna e2 will fail
        publisher.publish(new TripEventInfoDTO(3L, now(), CREATED, Region.AT_VIENNA)); // vienna e3 will time out
        publisher.publish(new TripEventInfoDTO(3L, now(), QUEUED, Region.AT_VIENNA)); // vienna e3 will time out
        publisher.publish(new TripEventInfoDTO(4L, now(), CREATED, Region.DE_BERLIN)); // berlin e4 will fail
        publisher.publish(new TripEventInfoDTO(4L, now(), QUEUED, Region.DE_BERLIN)); // berlin e4 will fail

        // s1 warning #1
        causeWarning.accept(1L, Region.AT_VIENNA);

        // s1 warning #2
        causeWarning.accept(2L, Region.AT_VIENNA);

        // s2 warning #1
        causeWarning.accept(4L, Region.DE_BERLIN);

        LOG.info("Checking that no alert has been issued yet");
        assertNull(alerts.poll(500));

        // make sure the other events don't time out
        publisher.publish(new TripEventInfoDTO(1L, now(), MATCHED, Region.AT_VIENNA)); // vienna e1 will fail
        publisher.publish(new TripEventInfoDTO(2L, now(), MATCHED, Region.AT_VIENNA)); // vienna e2 will fail
        publisher.publish(new TripEventInfoDTO(4L, now(), MATCHED, Region.DE_BERLIN)); // berlin e4 will fail

        sleep(4000); // waiting for e3 to time out

        publisher.dropClients();
        flinkExecution.get();

        LOG.info("Collecting Alert event");
        Alert alert = alerts.take();
        assertEquals("Expected only a single alert", 0, alerts.get().size());

        assertEquals(Region.AT_VIENNA, alert.getRegion());
        assertEquals("An alert should comprise three warnings", 3, alert.getWarnings().size());

        Warning w0 = alert.getWarnings().get(0);
        Warning w1 = alert.getWarnings().get(1);
        Warning w2 = alert.getWarnings().get(2);

        assertThat(w0, instanceOf(TripFailedWarning.class));
        assertThat(w1, instanceOf(TripFailedWarning.class));
        assertThat(w2, instanceOf(MatchingTimeoutWarning.class));

        assertEquals(Region.AT_VIENNA, w0.getRegion());
        assertEquals(Region.AT_VIENNA, w1.getRegion());
        assertEquals(Region.AT_VIENNA, w2.getRegion());
    }

    @Test
    public void averageMatchingDurationWindow_worksCorrectly() throws Exception {
        // makes sure the event is triggered at the correct instant

        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync();

        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        sleep(250);
        publisher.publish(new TripEventInfoDTO(1L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(2L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(3L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(4L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(5L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(6L, now(), CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(7L, now(), CREATED, Region.DE_BERLIN));

        sleep(100);
        publisher.publish(new TripEventInfoDTO(1L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(2L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(3L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(4L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(5L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(6L, now(), QUEUED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(7L, now(), QUEUED, Region.DE_BERLIN));

        sleep(100);
        publisher.publish(new TripEventInfoDTO(1L, now(), MATCHED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(2L, now(), MATCHED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(3L, now(), MATCHED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(4L, now(), MATCHED, Region.AT_VIENNA));

        // the first four events should not trigger anything
        LOG.info("Checking AverageMatchingDuration events after the first four MATCHED events of AT_VIENNA");
        assertNull(averageMatchingDurations.poll(500));

        // this event is from a different region
        publisher.publish(new TripEventInfoDTO(7L, now(), MATCHED, Region.DE_BERLIN));
        LOG.info("Checking AverageMatchingDuration events after the first MATCHED event of DE_BERLIN");
        assertNull(averageMatchingDurations.poll(500));

        // fifth event in s1 triggers the window operation
        publisher.publish(new TripEventInfoDTO(5L, now(), MATCHED, Region.AT_VIENNA));
        LOG.info("Collecting AverageMatchingDuration event for AT_VIENNA");
        AverageMatchingDuration event = averageMatchingDurations.take();
        assertNotNull(event);
        assertEquals(Region.AT_VIENNA, event.getRegion());

        // should be in a new window and therefore not trigger
        publisher.publish(new TripEventInfoDTO(6L, now(), MATCHED, Region.AT_VIENNA));
        LOG.info("Checking AverageMatchingDuration events after the sixth MATCHED event of AT_VIENNA");
        assertNull(averageMatchingDurations.poll(500));

        publisher.dropClients();

        flinkExecution.get();
    }

    @Test
    public void averageMatchingDurations_areCalculatedCorrectly() throws Exception {
        // makes sure the keying works properly and that the calculation is done from MatchingDuration events
        // requires MatchingDuration events to be calculated correctly

        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync();

        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        List<MatchingDuration> viennaDurations = new ArrayList<>(5);
        List<MatchingDuration> berlinDurations = new ArrayList<>(5);

        sleep(250);
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, CREATED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, CREATED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(3L, t, CREATED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(4L, t, CREATED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(5L, t, CREATED, Region.AT_VIENNA));

        sleep(250);
        publisher.publish(5, t -> new TripEventInfoDTO(6L, t, CREATED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(7L, t, CREATED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(8L, t, CREATED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(9L, t, CREATED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(10L, t, CREATED, Region.DE_BERLIN));

        sleep(125);
        publisher.publish(5, t -> new TripEventInfoDTO(6L, t, QUEUED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(7L, t, QUEUED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(8L, t, QUEUED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(9L, t, QUEUED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(10L, t, QUEUED, Region.DE_BERLIN));

        sleep(125);
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(3L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(4L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(5L, t, QUEUED, Region.AT_VIENNA));

        publisher.publish(5, t -> new TripEventInfoDTO(6L, t, MATCHED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(7L, t, MATCHED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(8L, t, MATCHED, Region.DE_BERLIN));

        LOG.info("Collecting MatchingDuration events 1,2,3 for DE_BERLIN");
        berlinDurations.addAll(matchingDurations.take(3));

        sleep(500);
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(3L, t, MATCHED, Region.AT_VIENNA));

        LOG.info("Collecting MatchingDuration events 1,2,3 for AT_VIENNA");
        viennaDurations.addAll(matchingDurations.take(3));

        publisher.publish(5, t -> new TripEventInfoDTO(9L, t, MATCHED, Region.DE_BERLIN));
        publisher.publish(5, t -> new TripEventInfoDTO(10L, t, MATCHED, Region.DE_BERLIN));

        LOG.info("Collecting MatchingDuration events 4,5 for DE_BERLIN");
        berlinDurations.addAll(matchingDurations.take(2));

        sleep(500);
        publisher.publish(5, t -> new TripEventInfoDTO(4L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(5L, t, MATCHED, Region.AT_VIENNA));

        LOG.info("Collecting MatchingDuration events 4,5 for AT_VIENNA");
        viennaDurations.addAll(matchingDurations.take(2));

        LOG.info("Collecting AverageMatchingDuration event for DE_BERLIN");
        AverageMatchingDuration e0 = averageMatchingDurations.take(); // berlin

        LOG.info("Collecting AverageMatchingDuration event for AT_VIENNA");
        AverageMatchingDuration e1 = averageMatchingDurations.take(); // vienna

        assertEquals("Expected calculation for berlin to have been triggered first", e0.getRegion(), Region.DE_BERLIN);
        assertEquals("Expected calculation for vienna to have been triggered second", e1.getRegion(), Region.AT_VIENNA);

        assertEquals("Wrong number of MatchingDuration events for AT_VIENNA", 5, viennaDurations.size());
        assertEquals("Wrong number of MatchingDuration events for DE_BERLIN", 5, berlinDurations.size());

        double viennaAvg = viennaDurations.stream().mapToLong(MatchingDuration::getDuration).average().orElse(-1);
        double berlinAvg = berlinDurations.stream().mapToLong(MatchingDuration::getDuration).average().orElse(-1);

        assertEquals("Average duration was not calculated from MatchingDuration events correctly", e0.getDuration(),
            berlinAvg, 100);
        assertEquals("Average duration was not calculated from MatchingDuration events correctly", e1.getDuration(),
            viennaAvg, 100);

        publisher.dropClients();
        flinkExecution.get();
    }
}
