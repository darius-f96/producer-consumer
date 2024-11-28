package tpjad;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import org.apache.kafka.clients.producer.*;

import java.io.IOException;
import java.util.Properties;

@WebServlet("/producer")
public class ProducerServlet extends HttpServlet {
    private Producer<String, String> producer;

    @Override
    public void init() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String message = req.getParameter("message");
        producer.send(new ProducerRecord<>("alegeri-topic", message));
        resp.getWriter().write("Message sent to Kafka: " + message);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("This endpoint is working");
    }

    @Override
    public void destroy() {
        producer.close();
    }
}
