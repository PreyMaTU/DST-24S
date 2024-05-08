package dst.ass3.messaging;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

public interface IWorkloadMonitor extends Closeable {

    /**
     * Returns for each region the amount of waiting requests.
     *
     * @return a map
     */
    Map<Region, Long> getRequestCount();

    /**
     * Returns the amount of workers for each region. This can be deduced from the amount of consumers to each
     * queue.
     *
     * @return a map
     */
    Map<Region, Long> getWorkerCount();

    /**
     * Returns for each region the average processing time of the last 10 recorded requests. The data comes from
     * subscriptions to the respective topics.
     *
     * @return a map
     */
    Map<Region, Double> getAverageProcessingTime();

    @Override
    void close() throws IOException;
}
