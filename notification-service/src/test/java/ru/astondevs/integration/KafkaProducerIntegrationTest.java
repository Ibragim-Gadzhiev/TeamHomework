package ru.astondevs.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.astondevs.config.TestConfig;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.service.KafkaProducer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestConfig.class)
@EmbeddedKafka(partitions = 1, topics = {"userAdd-topic", "userDelete-topic"})
@DirtiesContext
class KafkaProducerIntegrationTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    private final BlockingQueue<String> userAddQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> userDeleteQueue = new LinkedBlockingQueue<>();

    @BeforeEach
    void setUp() throws Exception {
        registry.getListenerContainers().forEach(container -> {
            if (!container.isRunning()) {
                container.start();
            }
        });
        Thread.sleep(2000);
    }

    @KafkaListener(topics = "userAdd-topic", groupId = "testGroupAdd")
    public void listenUserAdd(String message) {
        userAddQueue.add(message);
        System.out.println("Received message in userAdd-topic: " + message);
    }

    @KafkaListener(topics = "userDelete-topic", groupId = "testGroupDelete")
    public void listenUserDelete(String message) {
        userDeleteQueue.add(message);
        System.out.println("Received message in userDelete-topic: " + message);
    }

    @AfterEach
    void tearDown() {
        registry.stop();
    }

    @Test
    void shouldSendUserAddEventToKafka() throws Exception {
        UserEventDto event = new UserEventDto("create", "unknown.nvme@gmail.com");

        kafkaProducer.sendUserAddEvent(event);

        String receivedMessage = userAddQueue.poll(15, TimeUnit.SECONDS);
        assertThat(receivedMessage)
                .as("Сообщение не было получено из топика userAdd-topic")
                .isNotNull();

        UserEventDto receivedEvent = objectMapper.readValue(receivedMessage, UserEventDto.class);
        assertThat(receivedEvent.operation()).isEqualTo("create");
        assertThat(receivedEvent.email()).isEqualTo("unknown.nvme@gmail.com");
    }

    @Test
    void shouldSendUserDeleteEventToKafka() throws Exception {
        UserEventDto event = new UserEventDto("delete", "unknown.nvme@gmail.com");

        kafkaProducer.sendUserDeleteEvent(event);

        String receivedMessage = userDeleteQueue.poll(15, TimeUnit.SECONDS);
        assertThat(receivedMessage)
                .as("Сообщение не было получено из топика userDelete-topic")
                .isNotNull();

        UserEventDto receivedEvent = objectMapper.readValue(receivedMessage, UserEventDto.class);
        assertThat(receivedEvent.operation()).isEqualTo("delete");
        assertThat(receivedEvent.email()).isEqualTo("unknown.nvme@gmail.com");
    }
}