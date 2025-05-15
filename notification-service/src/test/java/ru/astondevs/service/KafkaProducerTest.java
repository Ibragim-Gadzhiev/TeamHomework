package ru.astondevs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import ru.astondevs.config.KafkaConfig;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.service.impl.KafkaProducerImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private KafkaConfig kafkaConfig;

    private KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        kafkaProducer = new KafkaProducerImpl(kafkaTemplate, objectMapper, kafkaConfig);
    }

    @Test
    void shouldSendUserAddEventToKafka() throws Exception {
        String topic = "userAdd-topic";
        UserEventDto event = new UserEventDto("create", "unknown.nvme@gmail.com");

        Mockito.when(kafkaConfig.getUserAdd()).thenReturn(topic);

        kafkaProducer.sendUserAddEvent(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(topic);
        assertThat(messageCaptor.getValue()).contains("create");
        assertThat(messageCaptor.getValue()).contains("unknown.nvme@gmail.com");
    }

    @Test
    void shouldSendUserDeleteEventToKafka() throws Exception {
        String topic = "userDelete-topic";
        UserEventDto event = new UserEventDto("delete", "example@gmail.com");

        Mockito.when(kafkaConfig.getUserDelete()).thenReturn(topic);

        kafkaProducer.sendUserDeleteEvent(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(topic);
        assertThat(messageCaptor.getValue()).contains("delete");
        assertThat(messageCaptor.getValue()).contains("example@gmail.com");
    }

    @Test
    void shouldThrowExceptionWhenTopicIsNull() {
        UserEventDto event = new UserEventDto("create", "unknown.nvme@gmail.com");

        Mockito.when(kafkaConfig.getUserAdd()).thenReturn(null);

        assertThatThrownBy(() -> kafkaProducer.sendUserAddEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Название топика Kafka не может быть пустым");
    }

    @Test
    void shouldThrowExceptionWhenDtoFieldsAreNull() {
        UserEventDto event = new UserEventDto(null, null);

        String topic = "userAdd-topic";
        Mockito.when(kafkaConfig.getUserAdd()).thenReturn(topic);

        assertThatThrownBy(() -> kafkaProducer.sendUserAddEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Поля UserEventDto не должны быть пустыми");
    }
}