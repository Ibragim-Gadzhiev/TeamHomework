package ru.astondevs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.service.KafkaProducer;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.userAdd}")
    private String userAddTopic;

    @Value("${spring.kafka.topics.userDelete}")
    private String userDeleteTopic;

    @Override
    public void sendUserAddEvent(UserEventDto event) {
        sendEvent(userAddTopic, event);
    }

    @Override
    public void sendUserDeleteEvent(UserEventDto event) {
        sendEvent(userDeleteTopic, event);
    }

    private void sendEvent(String topic, UserEventDto event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Message sent to topic {}: {}", topic, message);
                        } else {
                            log.error("Failed to send message to topic {}: {}", topic, ex.getMessage(), ex);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for topic {}: {}", topic, e.getMessage());
            throw new SerializationException("Failed to serialize event", e);
        }
    }
}