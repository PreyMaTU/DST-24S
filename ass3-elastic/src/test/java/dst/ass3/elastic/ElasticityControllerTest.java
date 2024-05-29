package dst.ass3.elastic;

import dst.ass3.elastic.impl.ElasticityFactory;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElasticityControllerTest {
    private static final String WORKER_IMAGE = "dst/ass3-worker";

    IElasticityFactory factory;

    @Mock
    IContainerService containerService;
    @Mock
    IWorkloadMonitor workloadMonitor;

    IElasticityController elasticityController;
    Map<Region, Double> processingTimes = new HashMap<>();
    Map<Region, Long> workerCount = new HashMap<>();
    Map<Region, Long> requestCount = new HashMap<>();
    List<ContainerInfo> containers = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        factory = new ElasticityFactory();
        elasticityController = factory.createElasticityController(containerService, workloadMonitor);

        processingTimes.clear();
        processingTimes.put(Region.AT_VIENNA, 5000.0);
        processingTimes.put(Region.DE_BERLIN, 10000.0);
        processingTimes.put(Region.AT_LINZ, 2000.0);

        when(workloadMonitor.getAverageProcessingTime()).thenReturn(processingTimes);

        workerCount.clear();
        workerCount.put(Region.AT_VIENNA, 95L);
        workerCount.put(Region.DE_BERLIN, 87L);
        workerCount.put(Region.AT_LINZ, 61L);
        when(workloadMonitor.getWorkerCount()).thenReturn(workerCount);

        requestCount.clear();
        requestCount.put(Region.AT_VIENNA, 600L);
        requestCount.put(Region.DE_BERLIN, 1000L);
        requestCount.put(Region.AT_LINZ, 1005L);
        when(workloadMonitor.getRequestCount()).thenReturn(requestCount);

        containers.clear();
        for (int i = 0; i < 95; i++) {
            containers.add(containerInfo("vienna" + i, WORKER_IMAGE, Region.AT_VIENNA, true));
        }
        for (int i = 0; i < 87; i++) {
            containers.add(containerInfo("berlin" + i, WORKER_IMAGE, Region.DE_BERLIN, true));
        }
        for (int i = 0; i < 61; i++) {
            containers.add(containerInfo("linz" + i, WORKER_IMAGE, Region.AT_LINZ, true));
        }
        when(containerService.listContainers()).thenReturn(containers);
    }

    @After
    public void tearDown() {
        verify(workloadMonitor, atLeast(1)).getWorkerCount();
        verify(workloadMonitor, atLeast(1)).getRequestCount();
        verify(workloadMonitor, atLeast(1)).getAverageProcessingTime();
    }

    @Test
    public void notEnoughWorkers_scaleUp() throws Exception {
        // remove 10 vienna workers and 10 linz workers
        List<ContainerInfo> containersToRemove = containers.stream()
            .filter(c -> c.getContainerId().startsWith("vienna7") || c.getContainerId().startsWith("linz1"))
            .collect(Collectors.toList());
        containers.removeAll(containersToRemove);
        workerCount.put(Region.AT_VIENNA, 85L);
        workerCount.put(Region.AT_LINZ, 51L);

        elasticityController.adjustWorkers();

        verify(containerService, never()).stopContainer((String) any(String.class));
        verify(containerService, times(15)).startWorker(Region.AT_VIENNA);
        verify(containerService, times(16)).startWorker(Region.AT_LINZ);
        verify(containerService, never()).startWorker(Region.DE_BERLIN);
        verify(containerService, never()).listContainers();
    }

    @Test
    public void tooManyWorkers_scaleDown() throws Exception {
        // add 20 more, some should be stopped
        for (int i = 0; i < 20; i++) {
            containers.add(containerInfo("linz1" + i, WORKER_IMAGE, Region.AT_LINZ, true));
        }
        workerCount.put(Region.AT_LINZ, 81L);

        elasticityController.adjustWorkers();

        verify(containerService, times(14)).stopContainer(contains("linz"));
        verify(containerService, never()).stopContainer(contains("berlin"));
        verify(containerService, never()).stopContainer(contains("vienna"));
        verify(containerService, never()).startWorker((Region) any(Region.class));
        verify(containerService, times(1)).listContainers();
    }

    @Test
    public void justEnoughWorkers_doNotScale() throws Exception {
        elasticityController.adjustWorkers();
        verify(containerService, never()).startWorker((Region) any(Region.class));
        verify(containerService, never()).stopContainer((String) any());
        verify(containerService, never()).listContainers();
    }

    private ContainerInfo containerInfo(String id, String image, Region workerRegion, boolean running) {
        ContainerInfo info = new ContainerInfo();
        info.setContainerId(id);
        info.setImage(image);
        info.setWorkerRegion(workerRegion);
        info.setRunning(running);
        return info;
    }

}
