package ru.astondevs.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
public class UserService {
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
        validateEmail(dto.email());

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .age(dto.age())
                .build();
        User savedUser = userRepository.save(user);
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
        return convertToResponseDto(getUserEntity(id));
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO всех пользователей
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param id идентификатор пользователя
     * @param dto DTO с обновлёнными данными
     * @return DTO с данными обновлённого пользователя
     * @throws DuplicateEmailException если обновлённый email уже существует
     * @throws ResourceNotFoundException если пользователь не найден
     */
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        User user = getUserEntity(id);

        Optional.ofNullable(dto.name()).ifPresent(user::setName);

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            validateEmail(dto.email());
            user.setEmail(dto.email());
        }

        Optional.ofNullable(dto.age()).ifPresent(user::setAge);

        return convertToResponseDto(userRepository.save(user));
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @throws ResourceNotFoundException если пользователь не найден
     */
    @Transactional
    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("Пользователя с id: "
                    + id + " не существует");
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email " + email + " уже существует");
        }
    }

    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователя с id: "
                + id + " не существует"));
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
