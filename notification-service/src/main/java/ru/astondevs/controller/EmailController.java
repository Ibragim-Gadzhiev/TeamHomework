package ru.astondevs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.service.EmailNotificationService;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailNotificationService emailNotificationService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(@RequestParam String to,
                                          @RequestParam String subject,
                                          @RequestParam String body) {
        emailNotificationService.sendEmail(to, subject, body);
        return ResponseEntity.ok().build();
    }
}