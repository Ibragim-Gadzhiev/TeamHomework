package ru.astondevs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.notification.AccountStatusNotificationSender;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "userAdd-topic", groupId = "my-group")
    public void listenUserAddTopic(String message) {
        try {
            UserEventDto event = objectMapper.readValue(message, UserEventDto.class);
            AccountStatusNotificationSender.createNotifyAboutCreatingAccount(event.email());
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }

    @KafkaListener(topics = "userDelete-topic", groupId = "my-group")
    public void listenUserDeleteTopic(String message) {
        AccountStatusNotificationSender.createNotifyAboutDeletingAccount(message);
    }
}