package ru.astondevs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.service.EmailNotificationService;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailNotificationService emailNotificationService;

    @Tag(name = "Email API", description = "Управление отправкой email-уведомлений")
    @PostMapping("/send")
    @Operation(summary = "Отправить email", description = "Отправляет письмо на указанный адрес")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно отправлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<Void> sendEmail(
            @Parameter(description = "Данные для отправки email", required = true)
            @Valid @RequestBody EmailRequest request
    ) {
        emailNotificationService.sendEmail(request.to(), request.subject(), request.body());
        return ResponseEntity.ok().build();
    }
}

