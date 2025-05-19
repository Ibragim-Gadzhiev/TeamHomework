package ru.astondevs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Tag(name = "Email API", description = "Управление отправкой email-уведомлений")
    @Operation(
            summary = "Отправить email",
            description = "Отправляет письмо на указанный адрес",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно отправлено"),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            }
    )
    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(
            @Parameter(description = "Email получателя", required = true)
            @RequestParam String to,
            @Parameter(description = "Тема письма")
            @RequestParam String subject,
            @Parameter(description = "Текст письма")
            @RequestParam String body
    ) {
        emailNotificationService.sendEmail(to, subject, body);
        return ResponseEntity.ok().build();
    }
}