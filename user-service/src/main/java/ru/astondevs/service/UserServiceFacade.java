package ru.astondevs.service;

import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;

/**
 * Фасадный сервис для объединения операций с пользователями и взаимодействия с Kafka.
 */
public interface UserServiceFacade {

    /**
     * Создаёт пользователя и публикует событие о создании в Kafka.
     *
     * @param dto Данные для создания пользователя.
     * @return DTO созданного пользователя.
     */
    UserResponseDto createUserAndPublishEvent(UserCreateDto dto);

    /**
     * Удаляет пользователя и публикует событие об удалении в Kafka.
     *
     * @param id Идентификатор пользователя.
     */
    void deleteUserAndPublishEvent(Long id);
}