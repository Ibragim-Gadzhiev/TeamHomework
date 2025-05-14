package ru.astondevs.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.service.KafkaProducer;
import ru.astondevs.service.UserService;
import ru.astondevs.util.UserConverter;
import ru.astondevs.util.UserValidator;

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
    @Transactional
    public UserResponseDto createUserAndPublishEvent(UserCreateDto dto) {
        UserResponseDto createdUser = createUser(dto);
        kafkaProducer.sendUserAddEvent(new UserEventDto("create", dto.email()));
        return createdUser;
    }

    @Override
    @Transactional
    public void deleteUserAndPublishEvent(Long id) {
        UserResponseDto deletedUser = deleteAndReturnUserById(id);
        kafkaProducer.sendUserDeleteEvent(new UserEventDto("delete", deletedUser.email()));
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        log.info("Creating user with email: {}", dto.email());
        userValidator.validateCreateDto(dto);
        User user = userConverter.toEntity(dto);
        User savedUser = userRepository.save(user);
        log.info("Created user with id: {}", user.getId());
        return userConverter.toResponseDto(savedUser);
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
        log.info("Updated user id: {}", id);
        return userConverter.toResponseDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting user id: {}", id);
        User user = findUserOrThrow(id);
        userRepository.delete(user);
        log.info("Deleted user id: {}", id);
    }

    @Override
    @Transactional
    public UserResponseDto deleteAndReturnUserById(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
        return userConverter.toResponseDto(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
    }
}