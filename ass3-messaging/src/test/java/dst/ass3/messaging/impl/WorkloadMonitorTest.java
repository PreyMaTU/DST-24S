package dst.ass3.messaging.impl;

import com.rabbitmq.http.client.domain.QueueInfo;
import dst.ass3.messaging.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.Lifecycle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static dst.ass3.messaging.Constants.TOPIC_EXCHANGE;
import static dst.ass3.messaging.Constants.WORK_QUEUES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class WorkloadMonitorTest {

    private static final Logger LOG = LoggerFactory.getLogger(RequestGatewayTest.class);

    @Rule
    public RabbitResource rabbit = new RabbitResource();

    @Rule
    public Timeout timeout = new Timeout(60, TimeUnit.SECONDS);

    private IMessagingFactory factory;
    private IQueueManager queueManager;
    private IRequestGateway requestGateway;
    private IWorkloadMonitor workloadMonitor;

    @Before
    public void setUp() throws Exception {
        factory = new MessagingFactory();
        queueManager = factory.createQueueManager();
        requestGateway = factory.createRequestGateway();

        queueManager.setUp();

        workloadMonitor = factory.createWorkloadMonitor();
    }

    @After
    public void tearDown() throws Exception {
        queueManager.tearDown();

        requestGateway.close();
        queueManager.close();
        factory.close();
    }

    @Test
    public void getRequestCount_returnsCorrectCount() throws Exception {
        try {
            Map<Region, Long> countForRegion = new HashMap<>();
            for (Region region : Region.values()) {
                countForRegion.put(region, ThreadLocalRandom.current().nextLong(10, 20 + 1));
                for (long i = 0; i < countForRegion.get(region); i++) {
                    GeoPoint pickup = new GeoPoint(48.11, 16.22);
                    TripRequest request = new TripRequest("id" + i, region, pickup);
                    LOG.info("Sending request {}", request);
                    requestGateway.submitRequest(request);
                }
            }

            // wait for the messages to be processed by rabbit
            Thread.sleep(8000);
            assertThat(workloadMonitor.getRequestCount(), equalTo(countForRegion));
        } finally {
            workloadMonitor.close();
        }
    }

    @Test
    public void multipleWorkloadMonitors_uniqueQueueForEachMonitor() throws Exception {
        try (IWorkloadMonitor workloadMonitor2 = factory.createWorkloadMonitor();
             IWorkloadMonitor workloadMonitor3 = factory.createWorkloadMonitor();) {
            long nonWorkQueues = rabbit.getManager().getQueues().stream().filter(q -> !Arrays.asList(WORK_QUEUES).contains(q.getName())).count();
            assertThat(nonWorkQueues, greaterThanOrEqualTo(3L));
        } finally {
            workloadMonitor.close();
        }
    }

    @Test
    public void getAverageProcessingTime_correctAverageTime() throws Exception {
        try {
            Map<Region, Double> avgTimes = new HashMap<>();
            for (Region region : Region.values()) {
                long count = ThreadLocalRandom.current().nextLong(15, 25);
                long regionTime = 0;
                for (long i = 0; i < count; i++) {
                    long requestTime = ThreadLocalRandom.current().nextLong(1000, 20000 + 1);
                    if (count - i <= 10) {
                        regionTime += requestTime;
                    }

                    String body = String.format("{\"requestId\": \"%s\", \"processingTime\": \"%d\", \"driverId\": \"1\"}", UUID.randomUUID(), requestTime);
                    LOG.info("Sending request {}", body);
                    rabbit.getClient().convertAndSend(TOPIC_EXCHANGE, "requests." + region.toString().toLowerCase(), body);
                }
                avgTimes.put(region, ((double) regionTime / 10));
            }

            // wait for the messages to be processed by rabbit
            Thread.sleep(2000);

            assertThat(workloadMonitor.getAverageProcessingTime(), equalTo(avgTimes));
        } finally {
            workloadMonitor.close();
        }
    }

    @Test
    public void getWorkerCount_returnsCorrectCount() throws Exception {
        try {
            // spawn a random number of consumers
            Map<Region, Collection<MessageListenerContainer>> consumersForRegion = new HashMap<>();
            Map<Region, Long> consumerCountForRegion = new HashMap<>();
            for (Region region : Region.values()) {
                List<MessageListenerContainer> consumers = new ArrayList<>();
                consumersForRegion.put(region, consumers);
                consumerCountForRegion.put(region, ThreadLocalRandom.current().nextLong(10, 20 + 1));
                for (long i = 0; i < consumerCountForRegion.get(region); i++) {
                    consumers.add(spawnConsumer("dst." + region.toString().toLowerCase()));
                }
            }

            Thread.sleep(8000);
            Map<Region, Long> workerCount = workloadMonitor.getWorkerCount();

            // stop all consumers
            consumersForRegion.values().stream().flatMap(Collection::stream).forEach(Lifecycle::stop);

            assertThat(workerCount, equalTo(consumerCountForRegion));
        } finally {
            workloadMonitor.close();
        }
    }

    private MessageListenerContainer spawnConsumer(String queue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(rabbit.getConnectionFactory());
        container.addQueueNames(queue);
        container.start();
        return container;
    }

    @Test
    public void close_removesQueues() throws Exception {
        workloadMonitor.close();

        List<QueueInfo> queues = rabbit.getManager().getQueues();
        long nonWorkQueues = rabbit.getManager().getQueues().stream().filter(q -> !Arrays.asList(WORK_QUEUES).contains(q.getName())).count();
        assertThat(nonWorkQueues, is(0L));
    }
}
