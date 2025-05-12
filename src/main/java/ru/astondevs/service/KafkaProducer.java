package ru.astondevs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.astondevs.dto.UserEventDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendUserEvent(String topic, UserEventDto event) {
        try {
            String message = serializeEvent(event);
            kafkaTemplate.send(topic, message);
            log.info("Message sent to topic {}: {}", topic, message);
        } catch (Exception e) {
            log.error("Failed to send message to topic {}: {}", topic, e.getMessage());
            throw e;
        }
    }

    private String serializeEvent(UserEventDto event) {
        return String.format("{\"operation\":\"%s\",\"email\":\"%s\"}",
                event.operation(), event.email());
    }
}