package ru.astondevs.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO для обновления информации о пользователе.
 *
 * <p>Содержит следующие поля:
 * <ul>
 *     <li>{@code name} – имя пользователя (должно быть 2-50 символов, не должно быть пустым)</li>
 *     <li>{@code email} – адрес электронной почты (должен быть корректным и не быть пустым)</li>
 *     <li>{@code age} – возраст (от 0 до 120 лет)</li>
 * </ul>
 */
@Builder
public record UserUpdateDto(
        /**
         * Имя пользователя.
         */
        @Nullable
        @Size(min = 2, max = 50, message = "{name.incorrect}")
        String name,

        /**
         * Адрес электронной почты пользователя.
         */
        @Nullable
        @Email(message = "{email.incorrect}")
        String email,

        /**
         * Возраст пользователя.
         */
        @Nullable
        @Min(value = 0, message = "{min.age}") @Max(value = 120, message = "{max.age}")
        Integer age
) {
}
