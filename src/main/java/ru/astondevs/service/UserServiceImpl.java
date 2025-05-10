package ru.astondevs.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.util.UserConverter;
import ru.astondevs.util.UserValidator;

/**
 * Сервисный класс для управления пользователями.
 * Обеспечивает CRUD-операции над пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserConverter userConverter;

    /**
     * Создаёт нового пользователя.
     *
     * @param dto DTO с данными нового пользователя
     * @return DTO с данными сохранённого пользователя
     * @throws DuplicateEmailException если указанный email уже существует
     */
    @Override
    public UserResponseDto createUser(UserCreateDto dto) {
        userValidator.validateCreateDto(dto);
        User user = userConverter.toEntity(dto);
        User savedUser = userRepository.save(user);
        log.info("Created user with id: {}", user.getId());
        return userConverter.toResponseDto(savedUser);
    }

    /**
     * Возвращает пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return DTO с данными пользователя
     * @throws ResourceNotFoundException если пользователь с данным ID не найден
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователя с id: "
                        + id + " не существует"));
        return userConverter.toResponseDto(user);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO всех пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userConverter::toResponseDto)
                .toList();
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param id  идентификатор пользователя
     * @param dto DTO с обновлёнными данными
     * @return DTO с данными обновлённого пользователя
     * @throws DuplicateEmailException   если обновлённый email уже существует
     * @throws ResourceNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        userValidator.validateUpdateDto(dto);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователя с id: "
                        + id + " не существует"));

        userConverter.updateEntity(user, dto);
        User updatedUser = userRepository.save(user);
        log.info("Updated user id: {}", id);
        return userConverter.toResponseDto(updatedUser);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @throws ResourceNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользователя с id: "
                    + id + " не существует");
        }

        userRepository.deleteById(id);
        log.info("Deleted user id: {}", id);
    }
}
