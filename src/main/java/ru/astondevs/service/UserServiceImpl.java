package ru.astondevs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.util.UserConverter;
import ru.astondevs.util.UserValidator;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserConverter userConverter;
    private final KafkaProducer kafkaProducer;

    @Override
    public UserResponseDto createUser(UserCreateDto dto) {
        try {
            log.info("Creating user with email: {}", dto.email());
            userValidator.validateCreateDto(dto);
            User user = userConverter.toEntity(dto);
            User savedUser = userRepository.save(user);

            // Отправляем событие о создании
            kafkaProducer.sendUserEvent("userAdd-topic",  dto.email());

            log.info("Created user with id: {}", user.getId());
            return userConverter.toResponseDto(savedUser);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw e; // или кастомное исключение
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = findUserOrThrow(id);
        return userConverter.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userConverter::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        log.info("Updating user id: {}", id);
        userValidator.validateUpdateDto(dto);
        User user = findUserOrThrow(id);
        userConverter.updateEntity(user, dto);
        User updatedUser = userRepository.save(user);
        log.info("Updated user id: {}", id);
        return userConverter.toResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting user id: {}", id);
        User user = findUserOrThrow(id);
        String userEmail = user.getEmail(); // Сохраняем email перед удалением
        userRepository.delete(user);

        // Отправляем событие об удалении
        kafkaProducer.sendUserEvent("userDelete-topic", userEmail);

        log.info("Deleted user id: {}", id);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
    }
}