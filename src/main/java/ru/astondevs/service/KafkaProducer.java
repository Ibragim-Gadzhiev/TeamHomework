package ru.astondevs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.astondevs.config.KafkaConfig;
import ru.astondevs.dto.UserEventDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaConfig kafkaConfig;

    public void sendUserAddEvent(UserEventDto event) {
        sendEvent(kafkaConfig.getUserAddTopic(), event);
    }

    public void sendUserDeleteEvent(UserEventDto event) {
        sendEvent(kafkaConfig.getUserDeleteTopic(), event);
    }

    private void sendEvent(String topic, UserEventDto event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
            log.info("Message sent to topic {}: {}", topic, message);
        } catch (Exception e) {
            log.error("Failed to send message to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }
}