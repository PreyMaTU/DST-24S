package dst.ass2.service.auth.grpc;

import java.io.IOException;

/**
 * An implementation of this interface is expected by the application to start the grpc server. Inject get
 * {@link GrpcServerProperties} to access the configuration.
 */
public interface IGrpcServerRunner {
    /**
     * Starts the gRPC server.
     *
     * @throws IOException start error
     */
    void run() throws IOException;
}
