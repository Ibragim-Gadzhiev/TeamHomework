package ru.astondevs.service;

/**
 * Интерфейс для отправки email-уведомлений.
 * Определяет методы для отправки электронных писем.
 */
public interface EmailNotificationService {

    /**
     * Отправляет электронное письмо.
     *
     * @param to      Адрес получателя.
     * @param subject Тема письма.
     * @param body    Тело письма.
     * @throws ru.astondevs.exception.EmailSendingException Если отправка письма не удалась.
     */
    void sendEmail(String to, String subject, String body);
}