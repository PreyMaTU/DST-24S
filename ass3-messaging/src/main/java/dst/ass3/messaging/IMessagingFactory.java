package dst.ass3.messaging;

import java.io.Closeable;
import java.io.IOException;

public interface IMessagingFactory extends Closeable {

    IQueueManager createQueueManager();

    IRequestGateway createRequestGateway();

    IWorkloadMonitor createWorkloadMonitor();

    /**
     * Closes any resource the factory may create.
     *
     * @throws IOException propagated exceptions
     */
    @Override
    void close() throws IOException;
}
