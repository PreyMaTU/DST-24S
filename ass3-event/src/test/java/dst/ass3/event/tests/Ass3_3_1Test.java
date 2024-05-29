package dst.ass3.event.tests;

import dst.ass3.event.Ass3EventTestBase;
import dst.ass3.event.EventProcessingFactory;
import dst.ass3.event.IEventSourceFunction;
import dst.ass3.event.dto.TripEventInfoDTO;
import dst.ass3.event.model.domain.ITripEventInfo;
import dst.ass3.event.model.domain.Region;
import dst.ass3.event.model.domain.TripState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class Ass3_3_1Test extends Ass3EventTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(Ass3_3_1Test.class);

    @Rule
    public Timeout timeout = new Timeout(15, TimeUnit.SECONDS);

    private IEventSourceFunction sourceFunction;

    @Before
    public void setUp() throws Exception {
        sourceFunction = EventProcessingFactory.createEventSourceFunction();
        assertNotNull("EventProcessingFactory#createEventSourceFunction() not implemented", sourceFunction);
    }

    @Test
    public void open_shouldConnectToSubscriber() throws Exception {
        assertEquals(
            "IEventSourceFunction should not be connected upon construction",
            0, publisher.getConnectedClientCount()
        );

        sourceFunction.open(new Configuration());
        publisher.waitForClients();

        assertEquals(
            "Expected IEventSourceFunction to connect to publisher after open is called",
            1, publisher.getConnectedClientCount()
        );
    }

    @Test
    public void run_shouldCollectPublishedEvents() throws Exception {
        sourceFunction.open(new Configuration());
        publisher.waitForClients();

        Future<List<ITripEventInfo>> result = executor.submit(() -> {
            MockContext<ITripEventInfo> ctx = new MockContext<>();
            LOG.info("Running IEventSourceFunction with MockContext");
            sourceFunction.run(ctx);
            LOG.info("Done running IEventSourceFunction, returning collected events");
            return ctx.collected;
        });

        publisher.publish(new TripEventInfoDTO(1L, 0L, TripState.CREATED, Region.AT_VIENNA));
        publisher.publish(new TripEventInfoDTO(2L, 0L, TripState.CREATED, Region.DE_BERLIN));

        sleep(1000);

        LOG.info("Calling cancel on SourceFunction");
        sourceFunction.cancel();

        LOG.info("Dropping subscriber connections");
        publisher.dropClients();

        LOG.info("Calling close on SourceFunction");
        sourceFunction.close();

        List<ITripEventInfo> collected = result.get();
        assertEquals(2, collected.size());

        ITripEventInfo e0 = collected.get(0);
        ITripEventInfo e1 = collected.get(1);

        assertEquals(1L, e0.getTripId(), 0);
        assertEquals(2L, e1.getTripId(), 0);
    }

    @Test
    public void shouldBeSerializable() throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream())) {
            out.writeObject(sourceFunction);
            out.flush();
        } catch (NotSerializableException e) {
            fail("Implementation of IEventSourceFunction is not serializable");
        }
    }

    private static class MockContext<T> implements SourceFunction.SourceContext<T> {

        private final Object checkpointLock = new Object();

        private List<T> collected = new ArrayList<>();

        public List<T> getCollected() {
            return collected;
        }

        @Override
        public void collect(T element) {
            collected.add(element);
        }

        @Override
        public void collectWithTimestamp(T element, long timestamp) {
            collected.add(element);
        }

        @Override
        public void emitWatermark(Watermark mark) {

        }

        @Override
        public void markAsTemporarilyIdle() {

        }

        @Override
        public Object getCheckpointLock() {
            return checkpointLock;
        }

        @Override
        public void close() {

        }
    }

}
