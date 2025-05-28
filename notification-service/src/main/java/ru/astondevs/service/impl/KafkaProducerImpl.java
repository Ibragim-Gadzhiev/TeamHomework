package ru.astondevs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.astondevs.config.KafkaConfig;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.service.KafkaProducer;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaConfig kafkaConfig;

    @Value("${spring.kafka.topics.userAdd}")
    private String userAddTopic;

    @Value("${spring.kafka.topics.userDelete}")
    private String userDeleteTopic;

    @Override
    public void sendUserAddEvent(UserEventDto event) {
        kafkaTemplate.send(userAddTopic, convertToJson(event));
    }

    private String convertToJson(UserEventDto event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

    @Override
    public void sendUserDeleteEvent(UserEventDto event) {
        sendEvent(kafkaConfig.getUserDelete(), event);
    }

    private void sendEvent(String topic, UserEventDto event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
            log.info("Message sent to topic {}: {}", topic, message);
        } catch (Exception e) {
            log.error("Failed to send message to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Не удалось отправить сообщение в Kafka", e);
        }
    }

}