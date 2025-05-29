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
    @KafkaListener(
            topics = "${spring.kafka.topics.userAdd}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenUserAddTopic(String message) {
        processEvent(message, "Account Created", "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
    }

    @Override
    @KafkaListener(
            topics = "${spring.kafka.topics.userDelete}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenUserDeleteTopic(String message) {
        processEvent(message, "Account Deleted", "Здравствуйте! Ваш аккаунт был удалён.");
    }

    private void processEvent(String message, String subject, String body) {
        try {
            UserEventDto event = objectMapper.readValue(message, UserEventDto.class);
            log.info("Processing event: {}", event);

            if (!isValidEmail(event.email())) {
                log.warn("Invalid email in event: {}", event.email());
                return;
            }

            emailNotificationService.sendEmail(event.email(), subject, body);
            log.info("Email sent to {}", event.email());
        } catch (JsonParseException | JsonMappingException e) {
            log.error("Invalid JSON format: {} - {}", message, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing message: {} - {}", message, e.getMessage(), e);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }
}