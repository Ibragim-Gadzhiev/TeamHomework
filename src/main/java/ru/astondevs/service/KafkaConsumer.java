package ru.astondevs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.notification.AccountStatusNotificationSender;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final EmailNotificationService emailNotificationService;

    @KafkaListener(topics = "${kafka.topics.userAdd}", groupId = "${kafka.group-id}")
    public void listenUserAddTopic(String message) {
        try {
            UserEventDto event = objectMapper.readValue(message, UserEventDto.class);
            emailNotificationService.sendEmail(event.email(), "Account Created",
                    "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
        } catch (Exception e) {
            log.error("Error processing message from userAdd-topic: {}", message, e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.userDelete}", groupId = "${kafka.group-id}")
    public void listenUserDeleteTopic(String message) {
        try {
            UserEventDto event = objectMapper.readValue(message, UserEventDto.class);
            emailNotificationService.sendEmail(event.email(), "Account Deleted",
                    "Здравствуйте! Ваш аккаунт был удалён.");
        } catch (Exception e) {
            log.error("Error processing message from userDelete-topic: {}", message, e);
        }
    }
}