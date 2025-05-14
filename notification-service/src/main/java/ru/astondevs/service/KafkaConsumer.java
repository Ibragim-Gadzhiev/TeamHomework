package ru.astondevs.service;

/**
 * Интерфейс для обработки сообщений из Kafka.
 * Определяет методы для обработки событий из различных топиков.
 */
public interface KafkaConsumer {

    /**
     * Обрабатывает сообщения топика userAdd.
     *
     * @param message Сообщение топика.
     */
    void listenUserAddTopic(String message);

    /**
     * Обрабатывает сообщения топика userDelete.
     *
     * @param message Сообщение топика.
     */
    void listenUserDeleteTopic(String message);
}