package ru.astondevs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.astondevs.dto.UserEventDto;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendUserEvent(String topic, UserEventDto event) {
        String message = serializeEvent(event);
        kafkaTemplate.send(topic, message);
    }

    private String serializeEvent(UserEventDto event) {
        return String.format("{\"operation\":\"%s\",\"email\":\"%s\"}",
                event.operation(), event.email());
    }
}