package dst.ass3.messaging;

import com.rabbitmq.http.client.Client;
import org.junit.rules.ExternalResource;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.net.URL;

public class RabbitResource extends ExternalResource {

    private RabbitAdmin admin;
    private Client manager;
    private CachingConnectionFactory connectionFactory;
    private RabbitTemplate client;

    @Override
    protected void before() throws Throwable {

        manager = new Client(new URL(Constants.RMQ_API_URL), Constants.RMQ_USER, Constants.RMQ_PASSWORD);

        connectionFactory = new CachingConnectionFactory(Constants.RMQ_HOST);
        connectionFactory.setUsername(Constants.RMQ_USER);
        connectionFactory.setPassword(Constants.RMQ_PASSWORD);

        client = new RabbitTemplate(connectionFactory);
        admin = new RabbitAdmin(connectionFactory);
    }

    @Override
    protected void after() {
        connectionFactory.destroy();
    }

    public Client getManager() {
        return manager;
    }

    public RabbitTemplate getClient() {
        return client;
    }

    public RabbitAdmin getAdmin() {
        return admin;
    }

    public CachingConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
