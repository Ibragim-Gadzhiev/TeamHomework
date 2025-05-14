package ru.astondevs.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.service.EmailNotificationService;
import ru.astondevs.service.KafkaConsumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final EmailNotificationService emailNotificationService;

    @Override
    @KafkaListener(topics = "${kafka.topics.userAdd}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenUserAddTopic(String message) {
        log.info("Received message from userAdd-topic: {}", message);
        processEvent(message, "Account Created",
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
    }

    @Override
    @KafkaListener(topics = "${kafka.topics.userDelete}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenUserDeleteTopic(String message) {
        log.info("Received message from userDelete-topic: {}", message);
        processEvent(message, "Account Deleted",
                "Здравствуйте! Ваш аккаунт был удалён.");
    }

    private void processEvent(String message, String subject, String body) {
        try {
            log.debug("Processing message: {}", message);
            UserEventDto event = objectMapper.readValue(message, UserEventDto.class);
            log.info("Deserialized event: email={}", event.email());

            if (isValidEmail(event.email())) {
                emailNotificationService.sendEmail(event.email(), subject, body);
                log.info("Email sent to user: {}", event.email());
            } else {
                log.warn("Invalid or missing email in event: {}", event);
            }
        } catch (JsonParseException | JsonMappingException e) {
            log.error("Invalid JSON format: {}", message, e);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }
}