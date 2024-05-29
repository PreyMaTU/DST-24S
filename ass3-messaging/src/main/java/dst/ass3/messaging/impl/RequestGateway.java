package dst.ass3.messaging.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IRequestGateway;
import dst.ass3.messaging.TripRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RequestGateway implements IRequestGateway {

    final private ObjectMapper mapper = new ObjectMapper();
    private Connection connection;
    private Channel channel;

    RequestGateway( ConnectionFactory connectionFactory ) {
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch(IOException | TimeoutException e) {
            throw new RuntimeException("Could not connect to RabbitMQ", e);
        }
    }

    @Override
    public void submitRequest(TripRequest request) {
        try {
            // Serialize the request
            final var message = mapper.writeValueAsString(request);

            // Publish the message
            final var queue = "dst." + request.getRegion().name().toLowerCase();
            channel.basicPublish("", queue, null, message.getBytes());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not map request to JSON", e);

        } catch (IOException e) {
            throw new RuntimeException("Could not publish message to RabbitMQ", e);
        }
    }

    @Override
    public void close() throws IOException {
        if( connection == null ) {
            return;
        }

        try {
            channel.close();
        } catch (TimeoutException ignored) {}
        channel = null;

        connection.close();
        connection = null;
    }
}
