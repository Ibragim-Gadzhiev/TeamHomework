package ru.astondevs.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service", path = "/api/email")
public interface NotificationServiceClient {

    @PostMapping("/send")
    void sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body);
}
