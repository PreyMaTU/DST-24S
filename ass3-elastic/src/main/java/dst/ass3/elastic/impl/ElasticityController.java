package dst.ass3.elastic.impl;

import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.IContainerService;
import dst.ass3.elastic.IElasticityController;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.util.stream.Collectors;

public class ElasticityController implements IElasticityController {

    // Max waiting times for all regions
    private final static long[] MAX_WAIT_TIMES= new long[Region.values().length];
    static {
        MAX_WAIT_TIMES[Region.AT_LINZ.ordinal()]= 30 * 1000L;
        MAX_WAIT_TIMES[Region.AT_VIENNA.ordinal()]= 30 * 1000L;
        MAX_WAIT_TIMES[Region.DE_BERLIN.ordinal()]= 120 * 1000L;
    }

    private final static double SCALE_OUT_THRESHOLD = 0.1;
    private final static double SCALE_DOWN_THRESHOLD = 0.05;

    private final IContainerService containerService;
    private final IWorkloadMonitor monitor;

    ElasticityController(IContainerService containerService, IWorkloadMonitor monitor) {
        this.containerService = containerService;
        this.monitor = monitor;
    }

    private Long computeRequiredWorkerCount( long workerCount, long requestCount, double averageTime, long maxWaitTime ) {
        double linearWaitingTime = requestCount * averageTime;
        double expectedTime = linearWaitingTime / workerCount;

        if(expectedTime > maxWaitTime * (1 + SCALE_OUT_THRESHOLD)) {
            return (long) Math.ceil(linearWaitingTime / maxWaitTime);

        } else if(expectedTime < maxWaitTime * (1 - SCALE_DOWN_THRESHOLD)) {
            return (long) Math.floor(linearWaitingTime / maxWaitTime);
        }

        return workerCount;
    }

    private void startWorkers( Region region, long count ) throws ContainerException {
        for( long i = 0; i < count; i++ ) {
            containerService.startWorker( region );
        }
    }

    private void stopWorkers( Region region, long count ) throws ContainerException {
        for( final var container : containerService.listContainers() ) {
            // We deleted enough containers
            if( count <= 0 ) {
                break;
            }

            // There is more to delete, and this container fits our region -> delete it
            if( container.getWorkerRegion() == region ) {
                containerService.stopContainer(container.getContainerId());
                count--;
            }
        }
    }

    @Override
    public void adjustWorkers() throws ContainerException {
        final var workerCounts = monitor.getWorkerCount();
        final var requestCounts = monitor.getRequestCount();
        final var averageTimes = monitor.getAverageProcessingTime();

        // Adjust each region
        for( final var region : Region.values()) {
            // Get statistics values for the current region
            final var workerCount= workerCounts.getOrDefault(region, 0L);
            final var requestCount= requestCounts.getOrDefault(region, 0L);
            final var averageTime= averageTimes.getOrDefault(region, 0.0);
            final var maxWaitTime= MAX_WAIT_TIMES[region.ordinal()];

            // Compute needed adjustment and perform it
            final var requiredWorkerCount = computeRequiredWorkerCount( workerCount, requestCount, averageTime, maxWaitTime );
            final var adjustment = requiredWorkerCount - workerCount;
            if(adjustment > 0) {
                startWorkers( region, adjustment );
            } else if(adjustment < 0) {
                stopWorkers( region, -adjustment );
            }
        }
    }
}
