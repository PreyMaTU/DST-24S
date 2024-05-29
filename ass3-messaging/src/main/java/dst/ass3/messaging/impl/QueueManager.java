package dst.ass3.messaging.impl;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IQueueManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueManager implements IQueueManager {
    private Connection connection;

    QueueManager(ConnectionFactory connectionFactory) {
        try {
            connection = connectionFactory.newConnection();

        } catch( IOException | TimeoutException e ) {
            throw new RuntimeException("Could not connect to RabbitMQ", e);
        }
    }

    @Override
    public void setUp() {
        try( final var channel = connection.createChannel()) {
            channel.exchangeDeclare(Constants.TOPIC_EXCHANGE, "topic");

            // Create the queues
            for( final var queue : Constants.WORK_QUEUES) {
                channel.queueDeclare(queue, false, false, false, null);
            }
        } catch( IOException | TimeoutException e ) {
            throw new RuntimeException("Could not setup RabbitMQ work queues", e);
        }
    }

    @Override
    public void tearDown() {
        try(final var channel = connection.createChannel()) {
            channel.exchangeDelete(Constants.TOPIC_EXCHANGE);

            for( var queue : Constants.WORK_QUEUES ) {
                channel.queueDelete(queue);
            }
        } catch( IOException | TimeoutException e ) {
            throw new RuntimeException("Could not delete RabbitMQ work queues", e);
        }
    }

    @Override
    public void close() throws IOException {
        if( connection != null ) {
            connection.close();
            connection= null;
        }
    }
}
