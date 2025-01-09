**Documentation for the Project: POC-Alegeri**

**Title:** Multi-Servlet Application with Kafka Integration

---

### Overview of the Application

This project demonstrates a distributed application architecture utilizing servlets and Kafka to facilitate communication between producers and consumers. The application consists of two producers and two consumers, each interacting with their designated Kafka topics. Additionally, a dispatcher component is included to manage polling on consumers and to send messages to producers. The dispatcher UI provides a user-friendly interface for interacting with the dispatcher.

The application leverages different servlet containers, including Wildfly, Jetty, and Tomcat, which are configured to host the servlets. Docker is used to containerize these components for easier deployment and management.

---

### Components of the Application

#### 1. **Producer Servlets**

- **Purpose:** Generate messages and send them to specific Kafka topics.
- **Implementation:** Each producer servlet corresponds to a unique Kafka topic.
- **Prototypes:**
  ```java
  void sendMessage(String topic, String message) throws KafkaException;
  ```
- **Exceptions:** Throws `KafkaException` for any issues related to message production.

#### 2. **Consumer Servlets**

- **Purpose:** Poll messages from Kafka topics and store them in a persistent storage.
- **Implementation:** Each consumer servlet is assigned a Kafka topic to monitor.
- **Prototypes:**
  ```java
  void startPolling(String topic) throws InterruptedException;
  void stopPolling(String topic);
  List<String> getMessages(String topic);
  ```
- **Exceptions:** Throws `InterruptedException` during polling operations.

#### 3. **Dispatcher**

- **Purpose:**
  - Start and stop consumer polling.
  - Send commands to producer servlets.
  - Acts as the central coordinator between UI, producers, and consumers.
- **Prototypes:**
  ```java
  void startConsumer(String consumerId) throws InvalidConsumerException;
  void stopConsumer(String consumerId);
  void sendToProducer(String producerId, String message) throws InvalidProducerException;
  ```
- **Endpoints Used:**
  - Consumer 1: `http://consumer:8080/tomcat-consumer-app/consumer`
  - Consumer 2: `http://consumer-jetty:8080/consumer`
  - Producer 1: `http://producer:8080/tomcat-producer-app/producer`
  - Producer 2: `http://producer-jetty:8080/producer`

#### 4. **Dispatcher UI**

- **Purpose:** A React-based web interface for managing the application.
- **Accessible At:** `http://localhost:8085/dispatcher-app`
- **Features:**
  - Buttons to start/stop consumers.
  - Input forms to send messages to producers.
  - Displays logs and operational status.

---

### Servlet Containers

#### **Wildfly**

- **Role:** Hosts core application components such as the producer and consumer servlets.
- **Configuration:**
  - Deployed as `.war` files.
  - Configured using `standalone.xml` to define Kafka connection properties and logging.
- **Setup Steps:**
  1. Copy `.war` files to `standalone/deployments`.
  2. Start Wildfly using the command:
     ```
     ./standalone.sh
     ```

#### **Jetty**

- **Role:** Provides lightweight hosting for specific servlet components.
- **Configuration:**
  - Embedded setup defined in Java code.
  - Port configuration in `jetty.xml`.
- **Setup Steps:**
  1. Start Jetty server with:
     ```
     java -jar start.jar
     ```
  2. Verify deployment via the admin console.

#### **Tomcat**

- **Role:** Additional servlet container for specific services.
- **Configuration:**
  - Uses `server.xml` for connector definitions.
  - `.war` files placed in `webapps` folder.
- **Setup Steps:**
  1. Deploy `.war` files.
  2. Start Tomcat with:
     ```
     ./catalina.sh run
     ```

---

### Docker Integration

Docker is used to containerize the application components, making deployment seamless and consistent.

#### Docker Configuration
- **Docker Compose File:** Manages all services, including Wildfly, Jetty, Tomcat, Kafka, and the dispatcher UI.
- **Ports Exposed:**
  - Wildfly: `8080`
  - Jetty: `8081`
  - Tomcat: `8082`
  - Kafka: `9092` (default broker port)
  - Dispatcher UI: `8085`

#### Setup Steps
1. Navigate to each component’s directory (`kafka-instance`, `producer-servlet`, `consumer-servlet`, etc.).
2. Start the Kafka instance first:
   ```
   cd kafka-instance
   docker-compose up -d
   ```
3. Start the other components in the following order:
   - Producers
   - Consumers
   - Dispatcher
   - Dispatcher UI
4. Use the following command for each directory:
   ```
   docker-compose up -d
   ```

---

### Application Workflow

![alt-text](https://github.com/darius-f96/tpjad-servlet/blob/main/dispatcher-ui/src/tpjad1.png)

1. **Frontend Interaction:**
   - The Dispatcher UI is accessed at `http://localhost:8085/dispatcher-app`.
   - Users can:
     - Start/stop consumers.
     - Send messages to producers.
2. **Dispatcher Communication:**
   - The dispatcher receives API calls from the UI and routes them to the appropriate producer or consumer endpoint.
   - Example:
     - UI sends a request to `/startConsumer`.
     - Dispatcher calls `http://consumer:8080/tomcat-consumer-app/consumer`.
3. **Kafka Integration:**
   - Producers send messages to their assigned Kafka topics.
   - Consumers poll messages from their assigned Kafka topics and store them persistently.

---

### Usage Instructions

#### Starting the Application
1. Ensure Docker and Docker Compose are installed on your system.
2. Start the components as described in the Docker setup steps.
3. Access the servlet endpoints via the exposed ports:
   - Wildfly: `http://localhost:8080`
   - Jetty: `http://localhost:8081`
   - Tomcat: `http://localhost:8082`
   - Dispatcher UI: `http://localhost:8085/dispatcher-app`

#### Running the Application

- Use the dispatcher to:
  - Start polling consumers:
    ```
    POST /dispatcher/startConsumer
    ```
  - Stop polling consumers:
    ```
    POST /dispatcher/stopConsumer
    ```
  - Send messages to producers:
    ```
    POST /dispatcher/sendToProducer
    ```

#### Example Test Scenarios

1. **Producer Test:**
   - Input: `POST /producer/sendMessage`
   - Expected Output: Message appears in Kafka topic.
2. **Consumer Test:**
   - Input: `POST /consumer/startPolling`
   - Expected Output: Messages stored in persistent storage.

---

### System Architecture

- **Input:** Messages from HTTP POST requests via Dispatcher UI.
- **Processing:** Dispatcher routes requests to the appropriate producer or consumer.
- **Output:**
  - Messages produced to Kafka.
  - Messages consumed and stored persistently.

---

### Conclusion

This project demonstrates a robust integration of servlets, Docker, Kafka, and a React-based UI, showcasing the capabilities of Wildfly, Jetty, and Tomcat. The architecture is modular, containerized for ease of deployment, and scalable. Detailed instructions and examples ensure ease of understanding and extendibility.
