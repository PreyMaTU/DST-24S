package dst.ass3.messaging.impl;

import dst.ass3.messaging.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestGatewayTest {

    private static final Logger LOG = LoggerFactory.getLogger(RequestGatewayTest.class);

    @Rule
    public RabbitResource rabbit = new RabbitResource();

    @Rule
    public Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

    private IMessagingFactory factory;
    private IQueueManager queueManager;
    private IRequestGateway requestGateway;

    @Before
    public void setUp() throws Exception {
        factory = new MessagingFactory();
        queueManager = factory.createQueueManager();
        requestGateway = factory.createRequestGateway();

        queueManager.setUp();
    }

    @After
    public void tearDown() throws Exception {
        queueManager.tearDown();

        requestGateway.close();
        queueManager.close();
        factory.close();
    }

    @Test
    public void submitRequest_routesRequestsToCorrectQueues() throws Exception {
        TripRequest r1 = new TripRequest("id1", Region.AT_VIENNA, new GeoPoint(34.22, -22.11));
        TripRequest r2 = new TripRequest("id2", Region.AT_VIENNA, new GeoPoint(98.2, -1.11));
        TripRequest r3 = new TripRequest("id3", Region.AT_LINZ, new GeoPoint(85.33, -25d));

        LOG.info("Sending request {}", r1);
        requestGateway.submitRequest(r1);
        LOG.info("Sending request {}", r2);
        requestGateway.submitRequest(r2);
        LOG.info("Sending request {}", r3);
        requestGateway.submitRequest(r3);

        LOG.info("Taking requests from queue {}", Constants.QUEUE_AT_VIENNA);
        Message m1 = rabbit.getClient().receive(Constants.QUEUE_AT_VIENNA, 1000);
        assertThat(m1, notNullValue());

        LOG.info("Taking requests from queue {}", Constants.QUEUE_AT_VIENNA);
        Message m2 = rabbit.getClient().receive(Constants.QUEUE_AT_VIENNA, 1000);
        assertThat(m2, notNullValue());

        LOG.info("Taking requests from queue {}", Constants.QUEUE_AT_LINZ);
        Message m3 = rabbit.getClient().receive(Constants.QUEUE_AT_LINZ, 1000);
        assertThat(m3, notNullValue());

        assertThat("Expected queue to be empty as no request for that region were issued",
            rabbit.getClient().receive(Constants.QUEUE_DE_BERLIN, 1000), nullValue());
    }


    @Test
    public void submitRequest_serializesIntoJsonFormat() throws Exception {
        TripRequest r1 = new TripRequest("id1", Region.AT_VIENNA, new GeoPoint(1d, 2d));
        TripRequest r2 = new TripRequest("id2", Region.AT_LINZ, new GeoPoint(3d, 4d));
        TripRequest r3 = new TripRequest("id3", Region.DE_BERLIN, new GeoPoint(5d, 6d));

        LOG.info("Sending request {}", r1);
        requestGateway.submitRequest(r1);
        LOG.info("Sending request {}", r2);
        requestGateway.submitRequest(r2);
        LOG.info("Sending request {}", r3);
        requestGateway.submitRequest(r3);

        LOG.info("Taking request from queue {}", Constants.QUEUE_AT_VIENNA);
        Message m1 = rabbit.getClient().receive(Constants.QUEUE_AT_VIENNA, 1000);
        assertThat(m1, notNullValue());
        assertThat(new String(m1.getBody()),
            equalTo("{\"id\":\"id1\",\"region\":\"AT_VIENNA\",\"pickup\":{\"longitude\":1.0,\"latitude\":2.0}}"));

        LOG.info("Taking request from queue {}", Constants.QUEUE_AT_LINZ);
        Message m2 = rabbit.getClient().receive(Constants.QUEUE_AT_LINZ, 1000);
        assertThat(m2, notNullValue());
        assertThat(new String(m2.getBody()),
            equalTo("{\"id\":\"id2\",\"region\":\"AT_LINZ\",\"pickup\":{\"longitude\":3.0,\"latitude\":4.0}}"));

        LOG.info("Taking request from queue {}", Constants.QUEUE_DE_BERLIN);
        Message m3 = rabbit.getClient().receive(Constants.QUEUE_DE_BERLIN, 1000);
        assertThat(m3, notNullValue());
        assertThat(new String(m3.getBody()),
            equalTo("{\"id\":\"id3\",\"region\":\"DE_BERLIN\",\"pickup\":{\"longitude\":5.0,\"latitude\":6.0}}"));

    }
}
