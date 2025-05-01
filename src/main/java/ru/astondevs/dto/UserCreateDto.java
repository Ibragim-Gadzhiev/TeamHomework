package ru.astondevs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO для создания нового пользователя.
 *
 * <p>Используется для передачи данных при создании нового пользователя</p>
 *
 * <p>Содержит следующие поля:
 * <ul>
 *     <li>{@code name} – имя пользователя (должно быть 2-50 символов, не должно быть пустым)</li>
 *     <li>{@code email} – адрес электронной почты (должен быть корректным и не быть пустым)</li>
 *     <li>{@code age} – возраст (от 0 до 120 лет)</li>
 * </ul>
 */
@Builder
public record UserCreateDto(
        /**
         * Имя пользователя(2-50 символов).
         */
        @NotBlank(message = "{name.not.found}")
        @Size(min = 2, max = 50, message = "{name.incorrect}")
        String name,

        /**
         * Адрес электронной почты пользователя.
         */
        @NotBlank(message = "{email.not.found}")
        @Email(message = "{email.incorrect}")
        String email,

        /**
         * Возраст пользователя(0-120 символов).
         */
        @Min(value = 0, message = "{min.age}") @Max(value = 120, message = "{max.age}")
        int age
) {
}
