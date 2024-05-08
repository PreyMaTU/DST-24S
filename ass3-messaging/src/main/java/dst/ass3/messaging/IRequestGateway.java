package dst.ass3.messaging;

import java.io.Closeable;
import java.io.IOException;

public interface IRequestGateway extends Closeable {

    /**
     * Serializes and routes a request to the correct queue.
     *
     * @param request the request
     */
    void submitRequest(TripRequest request);

    /**
     * Closes any resources that may have been initialized (connections, channels, etc.)
     *
     * @throws IOException propagated exceptions
     */
    @Override
    void close() throws IOException;
}
