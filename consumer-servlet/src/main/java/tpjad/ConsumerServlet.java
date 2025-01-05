package tpjad;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import org.apache.kafka.clients.consumer.*;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@WebServlet("/consumer")
public class ConsumerServlet extends HttpServlet {
    private Consumer<String, String> consumer;
    private Thread consumerThread;
    private volatile boolean isPolling = false;

    // Thread-safe list to store received messages
    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();

    @Override
    public void init() {
        // Kafka Consumer configuration
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092"); // Kafka broker
        props.put("group.id", "consumer-group"); // Consumer group ID
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest"); // Read messages from the beginning

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("tomcat-topic")); // Subscribe to topic
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        if ("start".equalsIgnoreCase(action)) {
            if (isPolling) {
                resp.getWriter().write("Polling is already running.");
                return;
            }
            startPolling();
            resp.getWriter().write("Polling started successfully.");
        } else if ("stop".equalsIgnoreCase(action)) {
            if (!isPolling) {
                resp.getWriter().write("Polling is not running.");
                return;
            }
            stopPolling();
            resp.getWriter().write("Polling stopped successfully.");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid action. Use 'start' or 'stop'.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Return the list of received messages
        resp.setContentType("application/json");
        resp.getWriter().write(receivedMessages.toString());
    }

    private void startPolling() {
        isPolling = true;
        consumerThread = new Thread(() -> {
            while (isPolling) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        receivedMessages.add(record.value()); // Add message to the list
                        System.out.printf("Received message: %s from partition: %d%n", record.value(), record.partition());
                    }
                } catch (Exception e) {
                    System.err.println("Error during polling: " + e.getMessage());
                }
            }
        });
        consumerThread.start();
    }

    private void stopPolling() {
        isPolling = false;
        if (consumerThread != null) {
            consumerThread.interrupt();
        }
    }

    @Override
    public void destroy() {
        stopPolling();
        if (consumer != null) {
            consumer.close();
        }
    }
}
