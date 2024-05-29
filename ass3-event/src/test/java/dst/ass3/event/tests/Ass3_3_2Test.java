package dst.ass3.event.tests;

import dst.ass3.event.EventProcessingTestBase;
import dst.ass3.event.dto.TripEventInfoDTO;
import dst.ass3.event.model.domain.Region;
import dst.ass3.event.model.events.LifecycleEvent;
import org.apache.flink.api.common.JobExecutionResult;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static dst.ass3.event.model.domain.TripState.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ass3_3_2Test extends EventProcessingTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(Ass3_3_2Test.class);

    @Test
    public void lifecycleEventStream_worksCorrectly() throws Exception {
        // run flink in new thread
        Future<JobExecutionResult> flinkExecution = initAndExecuteAsync();

        // wait until a subscriber connects
        LOG.info("Waiting for subscribers to connect");
        publisher.waitForClients();

        LifecycleEvent event;

        long then = System.currentTimeMillis();

        // causes LifecycleEvent 0
        LOG.info("Publishing e0");
        publisher.publish(new TripEventInfoDTO(0L, then, CREATED, Region.AT_VIENNA));

        LOG.info("Collecting LifecycleEvent for e0");
        event = lifecycleEvents.take();
        LOG.info("Round-trip took {}ms", System.currentTimeMillis() - then);

        assertEquals("Event ID not set correctly", 0L, event.getTripId());
        assertEquals("State not set correctly", CREATED, event.getState());
        assertEquals("Region not set correctly", Region.AT_VIENNA, event.getRegion());
        assertThat("Timestamp not set correctly", event.getTimestamp(), Matchers.greaterThanOrEqualTo(then));
        assertThat("Timestamp not set correctly", event.getTimestamp(),
            Matchers.lessThanOrEqualTo(System.currentTimeMillis()));

        // should be filtered
        LOG.info("Publishing e1, should be filtered");
        publisher.publish(new TripEventInfoDTO(1L, now(), CREATED, null));

        assertNull("Events without a region should be filtered", lifecycleEvents.poll(500));

        // causes LifecycleEvent 1
        LOG.info("Publishing e2");
        publisher.publish(new TripEventInfoDTO(2L, now(), QUEUED, Region.DE_BERLIN));

        LOG.info("Collecting LifecycleEvent for e2");
        event = lifecycleEvents.take();
        assertEquals(2L, event.getTripId());
        assertEquals(QUEUED, event.getState());

        // should be filtered
        LOG.info("Publishing e3, should be filtered");
        publisher.publish(new TripEventInfoDTO(3L, now(), CREATED, null));
        assertNull("Events without a region should be filtered", lifecycleEvents.poll(500));

        // causes LifecycleEvent 2
        LOG.info("Publishing e4");
        publisher.publish(new TripEventInfoDTO(4L, now(), MATCHED, Region.AT_LINZ));

        LOG.info("Collecting LifecycleEvent for e4");
        event = lifecycleEvents.take();
        assertEquals(4L, event.getTripId());
        assertEquals(MATCHED, event.getState());

        // disconnect subscribers
        publisher.dropClients();

        // wait for execution to end
        flinkExecution.get();
    }
}
