package ru.astondevs.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "UserResponse", description = "Данные пользователя")
public record UserResponseDto(
        @Schema(description = "ID пользователя", example = "1")
        Long id,
        @Schema(description = "Имя", example = "Иван Иванов")
        String name,
        @Schema(description = "Email", example = "user@example.com")
        String email,
        @Schema(description = "Возраст", example = "30")
        Integer age,
        @Schema(description = "Дата создания")
        LocalDateTime createdAt
) {
}