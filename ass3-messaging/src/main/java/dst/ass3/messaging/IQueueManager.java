package dst.ass3.messaging;

import java.io.Closeable;
import java.io.IOException;

/**
 * Responsible for creating and tearing down all necessary RabbitMQ queues or exchanges necessary for running the system.
 */
public interface IQueueManager extends Closeable {

    /**
     * Initializes all queues or topic exchanges necessary for running the system.
     */
    void setUp();

    /**
     * Removes all queues or topic exchanged associated with the system.
     */
    void tearDown();

    /**
     * Closes underlying conection or resources, if any.
     *
     * @throws IOException propagated exceptions
     */
    @Override
    void close() throws IOException;
}
