package ru.astondevs.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.repository.UserRepository;

/**
 * Сервисный класс для управления пользователями.
 * Обеспечивает CRUD-операции над пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {
    private final UserRepository userRepository;

    /**
     * Создаёт нового пользователя.
     *
     * @param dto DTO с данными нового пользователя
     * @return DTO с данными сохранённого пользователя
     * @throws DuplicateEmailException если указанный email уже существует
     */
    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        log.debug("Attempt to create user with email: {}", dto.email());
        validateEmail(dto.email());

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .age(dto.age())
                .build();
        User savedUser = userRepository.save(user);
        log.info("User created successfully. ID: {}", savedUser.getId());
        return convertToResponseDto(savedUser);
    }

    /**
     * Возвращает пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return DTO с данными пользователя
     * @throws ResourceNotFoundException если пользователь с данным ID не найден
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return convertToResponseDto(getUserEntity(id));
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO всех пользователей
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.debug("Fetching all users");
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .toList();
        log.info("Found {} users in total", users.size());
        return users;
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
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        log.info("Updating user ID: {}. Update data: {}", id, dto);
        User user = getUserEntity(id);

        Optional.ofNullable(dto.name()).ifPresent(name -> {
            log.debug("Updating name for user {}: {}", id, name);
            user.setName(name);
        });

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            log.debug("Email change requested for user {}: {}", id, dto.email());
            validateEmail(dto.email());
            user.setEmail(dto.email());
        }

        Optional.ofNullable(dto.age()).ifPresent(age -> {
            log.debug("Updating age for user {}: {}", id, age);
            user.setAge(age);
        });

        User updatedUser = userRepository.save(user);
        log.info("User {} updated successfully", id);
        return convertToResponseDto(updatedUser);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @throws ResourceNotFoundException если пользователь не найден
     */
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting user with ID: {}", id);
        try {
            userRepository.deleteById(id);
            log.debug("User {} deleted successfully", id);
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Delete failed: User with ID {} not found", id);
            throw new ResourceNotFoundException("Пользователя с id: "
                    + id + " не существует");
        }
    }

    private void validateEmail(String email) {
        log.debug("Validating email uniqueness: {}", email);
        if (userRepository.existsByEmail(email)) {
            log.error("Email conflict detected: {}", email);
            throw new DuplicateEmailException("Email " + email + " уже существует");
        }
    }

    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("Пользователя с id: "
                                    + id + " не существует");
                });
    }

    private UserResponseDto convertToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
