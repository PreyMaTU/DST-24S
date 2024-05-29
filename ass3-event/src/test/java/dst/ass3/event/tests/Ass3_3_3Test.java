package dst.ass3.event.tests;

import dst.ass3.event.EventProcessingTestBase;
import dst.ass3.event.dto.TripEventInfoDTO;
import dst.ass3.event.model.domain.Region;
import dst.ass3.event.model.events.LifecycleEvent;
import dst.ass3.event.model.events.MatchingDuration;
import dst.ass3.event.model.events.MatchingTimeoutWarning;
import dst.ass3.event.model.events.TripFailedWarning;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static dst.ass3.event.model.domain.TripState.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ass3_3_3Test extends EventProcessingTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(Ass3_3_3Test.class);

    @Test
    public void matchingDurations_areCalculatedCorrectly() throws Exception {
        // tests whether duration calculation and stream keying works correctly
        // expects LifecycleEvent stream to work correctly

        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync();

        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        LifecycleEvent e1Start;
        LifecycleEvent e2Start;
        LifecycleEvent e1End;
        LifecycleEvent e2End;

        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, CREATED, Region.AT_VIENNA)); // 1 starts before 2
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, CREATED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(3L, t, CREATED, Region.AT_VIENNA)); // 3 never finishes

        LOG.info("Waiting for LifecycleEvent for event 1 being CREATED");
        e1Start = lifecycleEvents.take();
        LOG.info("Waiting for LifecycleEvent for event 2 being CREATED");
        e2Start = lifecycleEvents.take();
        LOG.info("Waiting for LifecycleEvent for event 3 being CREATED");
        lifecycleEvents.take();

        sleep(500);
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, QUEUED, Region.AT_VIENNA));

        LOG.info("Waiting for LifecycleEvent for event 1 being QUEUED");
        lifecycleEvents.take();
        LOG.info("Waiting for LifecycleEvent for event 2 being QUEUED");
        lifecycleEvents.take();

        sleep(500); // event 2 took about ~1000ms
        publisher.publish(new TripEventInfoDTO(2L, now(), MATCHED, Region.AT_VIENNA)); // 2 finishes before 1

        LOG.info("Waiting for LifecycleEvent for event 2 being MATCHED");
        e2End = lifecycleEvents.take();

        sleep(500); // event 1 took about ~1500ms
        publisher.publish(new TripEventInfoDTO(1L, now(), MATCHED, Region.AT_VIENNA));

        LOG.info("Waiting for LifecycleEvent for event 1 being MATCHED");
        e1End = lifecycleEvents.take();

        LOG.info("Collecting MatchingDuration event for event 2");
        MatchingDuration d0 = matchingDurations.take();

        LOG.info("Collecting MatchingDuration event for event 1");
        MatchingDuration d1 = matchingDurations.take();

        assertEquals("Expected event 2 to finish first", 2L, d0.getEventId()); // event 2 finished before 1
        assertEquals("Expected event 1 to finish last", 1L, d1.getEventId());

        assertThat("Expected MatchingDuration to be >= 0", d0.getDuration(), greaterThan(0L));
        assertThat("Expected MatchingDuration to be >= 0", d1.getDuration(), greaterThan(0L));

        assertEquals("MatchingDuration was not calculated from LifecycleEvents correctly",
            e2End.getTimestamp() - e2Start.getTimestamp(), d0.getDuration(), 100);

        assertEquals("MatchingDuration was not calculated from LifecycleEvents correctly",
            e1End.getTimestamp() - e1Start.getTimestamp(), d1.getDuration(), 100);

        publisher.dropClients();
        flinkExecution.get();
    }

    @Test
    public void durationsWithInterleavedEvents_areCalculatedCorrectly() throws Exception {
        // tests whether CEP rule is tolerant towards multiple state changes between QUEUED and MATCHED

        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync(e ->
            e.setMatchingDurationTimeout(Time.seconds(1))
        );

        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, CREATED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, CREATED, Region.AT_VIENNA)); // never finishes

        // change state several times (tests another aspect of the CEP rule)
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));

        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, MATCHED, Region.AT_VIENNA));

        publisher.dropClients();
        flinkExecution.get();

        List<MatchingDuration> result = new ArrayList<>(matchingDurations.get());
        assertEquals("Expected one event to have finished", 1, result.size());

        MatchingDuration d0 = result.get(0);
        assertEquals("Expected event 1 to have finished", 1L, d0.getEventId());
    }

    @Test
    public void timeoutWarnings_areEmittedCorrectly() throws Exception {
        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync(e -> {
            e.setMatchingDurationTimeout(Time.seconds(1));
        });

        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        publisher.publish(new TripEventInfoDTO(1L, now(), CREATED, Region.AT_VIENNA)); // never finishes

        sleep(100);
        publisher.publish(new TripEventInfoDTO(2L, now(), CREATED, Region.AT_VIENNA)); // never finishes

        // confounding event
        sleep(100);
        publisher.publish(new TripEventInfoDTO(1L, now(), QUEUED, Region.AT_VIENNA));

        sleep(1500); // wait for event to time out

        publisher.dropClients();

        LOG.info("Waiting for Flink execution to end");
        flinkExecution.get();

        LOG.info("Collecting timeout warning for event 1");
        MatchingTimeoutWarning w1 = matchingTimeoutWarnings.take();

        LOG.info("Collecting timeout warning for event 2");
        MatchingTimeoutWarning w2 = matchingTimeoutWarnings.take();

        assertEquals("Expected event 1 to time out first", 1L, w1.getTripId());
        assertEquals("Expected event 2 to time out second", 2L, w2.getTripId());
    }

    @Test
    public void tripFailedWarnings_areEmittedCorrectly() throws Exception {
        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync();

        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        publisher.publish(0, t -> new TripEventInfoDTO(1L, t, CREATED, Region.AT_VIENNA));
        publisher.publish(0, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(0, t -> new TripEventInfoDTO(2L, t, CREATED, Region.AT_VIENNA));
        publisher.publish(0, t -> new TripEventInfoDTO(2L, t, QUEUED, Region.AT_VIENNA));

        // event 1 fail #1
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));

        // event 2 fail #1
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, QUEUED, Region.AT_VIENNA));

        // event 1 fail #2
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));

        // event 2 fail #2 and then success
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, QUEUED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(2L, t, MATCHED, Region.AT_VIENNA));

        LOG.info("Checking that no TripFailedWarning was issued yet");
        assertNull(tripFailedWarnings.poll(500));

        LOG.info("Triggering third failure");
        // event 1 fail #3
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, MATCHED, Region.AT_VIENNA));
        publisher.publish(5, t -> new TripEventInfoDTO(1L, t, QUEUED, Region.AT_VIENNA));

        LOG.info("Waiting for TripFailedWarning for event 1");
        TripFailedWarning warning = tripFailedWarnings.take();
        assertEquals(1L, warning.getEventId());
        assertEquals(Region.AT_VIENNA, warning.getRegion());

        publisher.dropClients();
        flinkExecution.get();
    }
}
