package ru.astondevs.dto;

import java.time.LocalDateTime;
import lombok.Builder;


/**
 * DTO для представления пользовательских данных в ответе.
 *
 * <p>Используется для возврата информации о пользователе клиенту после создания,
 * обновления или получения данных.</p>
 *
 * <p>Содержит следующие поля:
 * <ul>
 *     <li>{@code id} - уникальный идентификатор пользователя</li>
 *     <li>{@code name} - имя пользователя</li>
 *     <li>{@code email} - адрес электронной почты</li>
 *     <li>{@code age} - возраст</li>
 *     <li>{@code createdAt} - дата и время создания пользователя</li>
 * </ul>
 */
@Builder
public record UserResponseDto(
        /**
         * Уникальный идентификатор пользователя.
         */
        Long id,

        /**
         * Имя пользователя.
         */
        String name,

        /**
         * Адрес электронной почты.
         */
        String email,

        /**
         * Возраст пользователя.
         */
        int age,

        /**
         * Дата и время создания записи.
         */
        LocalDateTime createdAt

) {
}