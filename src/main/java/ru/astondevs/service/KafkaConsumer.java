package ru.astondevs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.service.EmailNotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final EmailNotificationService emailNotificationService;

    @KafkaListener(topics = "${kafka.topics.userAdd}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenUserAddTopic(String message) {
        processEvent(message, "Account Created", "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
    }

    @KafkaListener(topics = "${kafka.topics.userDelete}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenUserDeleteTopic(String message) {
        processEvent(message, "Account Deleted", "Здравствуйте! Ваш аккаунт был удалён.");
    }

    private void processEvent(String message, String subject, String body) {
        try {
            UserEventDto event = objectMapper.readValue(message, UserEventDto.class);
            emailNotificationService.sendEmail(event.email(), subject, body);
            log.info("Processed event for email: {}", event.email());
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }
}