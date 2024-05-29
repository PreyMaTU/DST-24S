package dst.ass3.messaging;

/**
 * Contains several constants related to the RabbitMQ infrastructure and expected names for queues, exchanges and
 * routing keys.
 */
public final class Constants {

    public static final String RMQ_HOST = "127.0.0.1";
    public static final String RMQ_PORT = "5672";
    public static final String RMQ_VHOST = "/";
    public static final String RMQ_USER = "dst";
    public static final String RMQ_PASSWORD = "dst";

    public static final String RMQ_API_PORT = "15672";
    public static final String RMQ_API_URL = "http://" + RMQ_HOST + ":" + RMQ_API_PORT + "/api/";

    public static final String QUEUE_AT_VIENNA = "dst.at_vienna";
    public static final String QUEUE_AT_LINZ = "dst.at_linz";
    public static final String QUEUE_DE_BERLIN = "dst.de_berlin";


    public static final String[] WORK_QUEUES = {
        QUEUE_AT_VIENNA,
        QUEUE_AT_LINZ,
        QUEUE_DE_BERLIN
    };

    public static final String TOPIC_EXCHANGE = "dst.workers";

    public static final String ROUTING_KEY_AT_VIENNA = "requests.at_vienna";
    public static final String ROUTING_KEY_AT_LINZ = "requests.at_linz";
    public static final String ROUTING_KEY_DE_BERLIN = "requests.de_berlin";

    private Constants() {
        // util class
    }
}
