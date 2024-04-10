package dst.ass2.service.auth.client;

/**
 * This class holds the host and port value used to connect to the gRPC server. The CDI context provides an instance
 * that you can inject into your implementation of {@link IAuthenticationClient}. The config values are loaded from the
 * application.properties file.
 */
public class AuthenticationClientProperties {

    private String host;
    private int port;

    public AuthenticationClientProperties() {

    }

    public AuthenticationClientProperties(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
