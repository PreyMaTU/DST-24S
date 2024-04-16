package dst.ass2.service.auth.grpc;

/**
 * This class holds the port value used to bind the gRPC server. The CDI context provides an instance that you can
 * inject into your implementation of {@link IGrpcServerRunner}. The config values are loaded from the
 * grpc.properties.
 */
public class GrpcServerProperties {

    private int port;

    public GrpcServerProperties() {
    }

    public GrpcServerProperties(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
