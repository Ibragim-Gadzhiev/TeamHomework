package ru.astondevs.service;

import ru.astondevs.dto.UserEventDto;

/**
 * Интерфейс для продюсера сообщений в Kafka.
 * Определяет методы для отправки событий в топики Kafka.
 */
public interface KafkaProducer {

    /**
     * Отправляет событие о добавлении пользователя в соответствующий топик.
     *
     * @param event DTO события пользователя.
     */
    void sendUserAddEvent(UserEventDto event);

    /**
     * Отправляет событие об удалении пользователя в соответствующий топик.
     *
     * @param event DTO события пользователя.
     */
    void sendUserDeleteEvent(UserEventDto event);
}