package ru.astondevs.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    private KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaProducer = new KafkaProducer(kafkaTemplate);
    }

    @Test
    void shouldSendCreateUserEventToKafka() {
        // Arrange
        String topic = "userAdd-topic";
        String email = "create@example.com";

        // Act
        kafkaProducer.sendUserEvent(topic,  email);

        // Assert
        verify(kafkaTemplate).send(topicCaptor.capture(), stringCaptor.capture());

        String capturedTopic = topicCaptor.getValue();
        String capturedMessage = stringCaptor.getValue();

        assertThat(capturedTopic).isEqualTo(topic);
        assertThat(capturedMessage).contains("create@example.com");

        System.out.println("Captured Kafka message: " + capturedMessage);
    }

    @Test
    void shouldSendDeleteUserEventToKafka() {
        // Arrange
        String topic = "userDelete-topic";
        String email = "delete@example.com";

        // Act
        kafkaProducer.sendUserEvent(topic, email);

        // Assert
        verify(kafkaTemplate).send(topicCaptor.capture(), stringCaptor.capture());

        String capturedTopic = topicCaptor.getValue();
        String capturedMessage = stringCaptor.getValue();

        assertThat(capturedTopic).isEqualTo(topic);
        assertThat(capturedMessage).contains("delete@example.com");
    }


}
