package dst.ass3.messaging.impl;

import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.*;

public class MessagingFactory implements IMessagingFactory {
    private final ConnectionFactory connectionFactory;

    public MessagingFactory() {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.RMQ_HOST);
        connectionFactory.setVirtualHost(Constants.RMQ_VHOST);
        connectionFactory.setPort(Integer.parseInt(Constants.RMQ_PORT));
        connectionFactory.setUsername(Constants.RMQ_USER);
        connectionFactory.setPassword(Constants.RMQ_PASSWORD);
    }

    @Override
    public IQueueManager createQueueManager() {
        return new QueueManager( connectionFactory );
    }

    @Override
    public IRequestGateway createRequestGateway() {
        return new RequestGateway( connectionFactory );
    }

    @Override
    public IWorkloadMonitor createWorkloadMonitor() {
        return new WorkloadMonitor( connectionFactory );
    }

    @Override
    public void close() {
        // implement if needed
    }
}
