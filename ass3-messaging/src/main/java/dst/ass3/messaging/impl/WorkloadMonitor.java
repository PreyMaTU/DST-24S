package dst.ass3.messaging.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;
import dst.ass3.messaging.WorkerResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class WorkloadMonitor implements IWorkloadMonitor {
    private static final Set<String> WORK_QUEUES = new HashSet<>(List.of(Constants.WORK_QUEUES));

    private final Client client;
    private Connection connection;
    private Channel channel;
    private String queue;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<Region, ArrayDeque<Long>> processingTimes= new HashMap<>();

    WorkloadMonitor(ConnectionFactory connectionFactory) {
        // Create RabbitMQ REST client
        try {
            client= new Client(new URL(Constants.RMQ_API_URL), Constants.RMQ_USER, Constants.RMQ_PASSWORD);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException( String.format("Could not create RabbitMQ client with bad URL %s", Constants.RMQ_API_URL), e);
        }

        // Connect to RabbitMQ
        try {
            connection= connectionFactory.newConnection();
            channel= connection.createChannel();
        } catch(IOException | TimeoutException e) {
            throw new RuntimeException("Could not connect to RabbitMQ", e);
        }

        // Subscribe to worker queues
        try {
            // Create queue that subscribes to all worker queue topics
            queue = channel.queueDeclare("", false, false, false, null).getQueue();
            for( final var workQueue : Constants.WORK_QUEUES) {
                final var topic = "requests" + workQueue.substring(workQueue.lastIndexOf('.')).toLowerCase();
                channel.queueBind(queue, Constants.TOPIC_EXCHANGE, topic);
            }

            channel.basicConsume(queue, true, this::handleMessage, consumerTag -> {});
        } catch(IOException e) {
            throw new RuntimeException("Could not create RabbitMQ subscribe queue", e);
        }
    }

    private Region nameToRegion(String topic) {
        return Region.valueOf(
                topic.substring(topic.lastIndexOf('.') + 1).toUpperCase()
        );
    }

    private void handleMessage( String consumerTag, Delivery delivery ) {
        final var deliverYKey = delivery.getEnvelope().getRoutingKey();
        final var region = nameToRegion(deliverYKey);

        WorkerResponse response;
        try {
            final var message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            response = mapper.readValue(message, WorkerResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse queue message", e);
        }

        // Insert new time item at the end of the ring buffer
        final var times = processingTimes.computeIfAbsent(region, k -> new ArrayDeque<>());
        times.addLast(response.getProcessingTime());

        // Only keep the last 10 values
        while(times.size() > 10) {
            times.removeFirst();
        }
    }

    private <T> Map<Region, T> getWorkerPropertiesPerRegion(Function<QueueInfo, T> getter, T defaultValue) {
        // Fill map with default value for all regions
        final var values = new HashMap<Region, T>();
        for( final var region : Region.values() ) {
            values.put(region, defaultValue);
        }

        // Go through all queues on the client
        for (var queue : client.getQueues()) {
            // Only handle known work queues (guaranties that it can be converted to a region)
            final var name= queue.getName();
            if( WORK_QUEUES.contains(name) ) {
                // Get the value for the queue item and store it in the map
                final var region= nameToRegion(name);
                final var value = getter.apply( queue );
                values.put(region, value);
            }
        }

        return values;
    }

    @Override
    public Map<Region, Long> getRequestCount() {
        return getWorkerPropertiesPerRegion(QueueInfo::getMessagesReady, 0L);
    }

    @Override
    public Map<Region, Long> getWorkerCount() {
        return getWorkerPropertiesPerRegion(QueueInfo::getConsumerCount, 0L);
    }

    @Override
    public Map<Region, Double> getAverageProcessingTime() {
        final var map = new HashMap<Region, Double>();
        for( final var entry : processingTimes.entrySet() ) {
            final var avgTime= entry.getValue().stream().mapToDouble(t -> t).average().orElse(0.0);
            map.put(entry.getKey(), avgTime );
        }
        return map;
    }

    @Override
    public void close() throws IOException {
        if( connection == null ) {
            return;
        }

        channel.queueDelete( queue );

        try {
            channel.close();
        } catch (TimeoutException ignored) {}
        channel = null;

        connection.close();
        connection = null;
    }
}
