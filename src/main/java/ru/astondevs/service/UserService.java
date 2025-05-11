package ru.astondevs.service;

import jakarta.validation.Valid;
import java.util.List;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;

/**
 * Сервис для управления пользователями.
 * Определяет CRUD-операции и валидацию данных.
 */
public interface UserService {
    /**
     * Создаёт нового пользователя.
     *
     * @param dto Данные для создания пользователя.
     * @return DTO созданного пользователя.
     * @throws ru.astondevs.exception.DuplicateEmailException Если email уже занят.
     */
    UserResponseDto createUser(@Valid UserCreateDto dto);

    /**
     * Возвращает пользователя по ID.
     *
     * @param id Идентификатор пользователя.
     * @return DTO найденного пользователя.
     * @throws ru.astondevs.exception.ResourceNotFoundException Если пользователь не найден.
     */
    UserResponseDto getUserById(Long id);

    /**
     * Возвращает список всех пользователей.
     *
     * @return Список DTO пользователей.
     */
    List<UserResponseDto> getAllUsers();

    /**
     * Обновляет данные пользователя.
     *
     * @param id Идентификатор пользователя.
     * @param dto Данные для обновления.
     * @return DTO обновлённого пользователя.
     * @throws ru.astondevs.exception.ResourceNotFoundException Если пользователь не найден.
     * @throws ru.astondevs.exception.DuplicateEmailException Если новый email уже занят.
     */
    UserResponseDto updateUser(Long id, @Valid UserUpdateDto dto);

    /**
     * Удаляет пользователя по ID.
     *
     * @param id Идентификатор пользователя.
     * @throws ru.astondevs.exception.ResourceNotFoundException Если пользователь не найден.
     */
    void deleteById(Long id);
}
