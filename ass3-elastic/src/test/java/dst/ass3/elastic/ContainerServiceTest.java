package dst.ass3.elastic;

import dst.ass3.elastic.impl.ElasticityFactory;
import dst.ass3.messaging.RabbitResource;
import dst.ass3.messaging.Region;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContainerServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerServiceTest.class);

    @Rule
    public RabbitResource rabbit = new RabbitResource();

    @Rule
    public Timeout timeout = new Timeout(30, TimeUnit.SECONDS);

    IContainerService containerService;
    IElasticityFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new ElasticityFactory();

        containerService = factory.createContainerService();

        rabbit.getAdmin().declareQueue(new Queue("dst.at_vienna"));
        rabbit.getAdmin().declareQueue(new Queue("dst.at_linz"));
        rabbit.getAdmin().declareQueue(new Queue("dst.de_berlin"));
    }

    @After
    public void tearDown() throws Exception {
        rabbit.getAdmin().deleteQueue("dst.at_vienna");
        rabbit.getAdmin().deleteQueue("dst.at_linz");
        rabbit.getAdmin().deleteQueue("dst.de_berlin");
    }

    @Test
    public void spawnListStop_lifecycleWorks() throws Exception {
        List<ContainerInfo> containers = containerService.listContainers();
        int initialContainerSize = containers.size();

        ContainerInfo c1 = containerService.startWorker(Region.AT_VIENNA);
        LOG.info("Started container {}", c1);

        ContainerInfo c2 = containerService.startWorker(Region.AT_LINZ);
        LOG.info("Started container {}", c2);

        ContainerInfo c3 = containerService.startWorker(Region.DE_BERLIN);
        LOG.info("Started container {}", c3);

        LOG.info("Waiting for containers to boot...");
        Thread.sleep(4000);

        containers = containerService.listContainers();

        assertThat(containers.size(), is(3 + initialContainerSize));

        LOG.info("Stopping containers...");
        containerService.stopContainer(c1.getContainerId());
        containerService.stopContainer(c2.getContainerId());
        containerService.stopContainer(c3.getContainerId());

        Thread.sleep(5000);

        containers = containerService.listContainers();
        assertThat(containers.size(), is(initialContainerSize));
    }

    @Test(expected = ContainerNotFoundException.class)
    public void stopNonExistingContainer_throwsException() throws Exception {
        containerService.stopContainer("Non-Existing-Id");
    }

    @Test
    public void listContainers_containsCompleteInfo() throws Exception {
        ContainerInfo c1 = containerService.startWorker(Region.AT_VIENNA);
        LOG.info("Started container {}", c1);
        LOG.info("Waiting for container to boot...");
        Thread.sleep(5000);
        List<ContainerInfo> containers = containerService.listContainers();
        ContainerInfo containerInfo = containers.stream()
            .filter(c -> c1.getContainerId().equals(c.getContainerId()))
            .findFirst().get();
        assertThat(containerInfo, notNullValue());
        assertThat(containerInfo.getImage(), equalTo("dst/ass3-worker"));
        assertThat(containerInfo.getWorkerRegion(), equalTo(Region.AT_VIENNA));
        assertThat(containerInfo.isRunning(), is(true));
        LOG.info("Stopping container...");
        containerService.stopContainer(containerInfo.getContainerId());
    }

}
