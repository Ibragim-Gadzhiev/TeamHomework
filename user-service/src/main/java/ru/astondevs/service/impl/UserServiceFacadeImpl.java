package ru.astondevs.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.service.KafkaProducer;
import ru.astondevs.service.UserService;
import ru.astondevs.service.UserServiceFacade;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceFacadeImpl implements UserServiceFacade {

    private final UserService userService;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public UserResponseDto createUserAndPublishEvent(UserCreateDto dto) {
        log.info("Creating user and publishing event for email: {}", dto.email());
        UserResponseDto createdUser = userService.createUser(dto);
        kafkaProducer.sendUserAddEvent(new UserEventDto("create", dto.email()));
        return createdUser;
    }

    @Override
    @Transactional
    public void deleteUserAndPublishEvent(Long id) {
        log.info("Deleting user and publishing event for user id: {}", id);
        UserResponseDto deletedUser = userService.deleteAndReturnUserById(id);
        kafkaProducer.sendUserDeleteEvent(new UserEventDto("delete", deletedUser.email()));
    }
}