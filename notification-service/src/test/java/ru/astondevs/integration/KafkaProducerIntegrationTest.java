package ru.astondevs.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.service.KafkaProducer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"userAdd-topic", "userDelete-topic"})
@DirtiesContext
class KafkaProducerIntegrationTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final BlockingQueue<String> userAddQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> userDeleteQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "userAdd-topic", groupId = "testGroupAdd")
    public void listenUserAdd(String message) {
        userAddQueue.add(message);
    }

    @KafkaListener(topics = "userDelete-topic", groupId = "testGroupDelete")
    public void listenUserDelete(String message) {
        userDeleteQueue.add(message);
    }

    @AfterEach
    void tearDown() {
        kafkaListenerEndpointRegistry.stop();
    }

    @Test
    void shouldSendUserAddEventToKafka() throws Exception {
        UserEventDto event = new UserEventDto("create", "unknown.nvme@gmail.com");

        kafkaProducer.sendUserAddEvent(event);

        String receivedMessage = userAddQueue.poll(15, TimeUnit.SECONDS);
        assertThat(receivedMessage).isNotNull();

        UserEventDto receivedEvent = objectMapper.readValue(receivedMessage, UserEventDto.class);
        assertThat(receivedEvent.operation()).isEqualTo("create");
        assertThat(receivedEvent.email()).isEqualTo("unknown.nvme@gmail.com");
    }

    @Test
    void shouldSendUserDeleteEventToKafka() throws Exception {
        UserEventDto event = new UserEventDto("delete", "unknown.nvme@gmail.com");

        kafkaProducer.sendUserDeleteEvent(event);

        String receivedMessage = userDeleteQueue.poll(15, TimeUnit.SECONDS);
        assertThat(receivedMessage).isNotNull();

        UserEventDto receivedEvent = objectMapper.readValue(receivedMessage, UserEventDto.class);
        assertThat(receivedEvent.operation()).isEqualTo("delete");
        assertThat(receivedEvent.email()).isEqualTo("unknown.nvme@gmail.com");
    }
}