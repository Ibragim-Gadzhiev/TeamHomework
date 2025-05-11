package ru.astondevs.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.astondevs.notification.AccountStatusNotificationSender;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "userAdd-topic", groupId = "my-group")
    public void listenUserAddTopic(String message) {
        AccountStatusNotificationSender.createNotifyAboutCreatingAccount(message);
    }
    @KafkaListener(topics = "userDelete-topic", groupId = "my-group")
    public void listenUserDeleteTopic(String message) {
        AccountStatusNotificationSender.createNotifyAboutDeletingAccount(message);
    }
}