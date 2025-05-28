package ru.astondevs.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.service.KafkaProducer;
import ru.astondevs.service.NotificationServiceClient;
import ru.astondevs.service.UserService;
import ru.astondevs.service.UserServiceFacade;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceFacadeImpl implements UserServiceFacade {
    private final UserService userService;
    private final KafkaProducer kafkaProducer;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final NotificationServiceClient notificationClient;

    public void someMethod() {
        circuitBreakerFactory.create("notificationCB").run(() -> {
            notificationClient.sendEmail("test@example.com", "Subject", "Body");
            return null;
        });
    }

    @Override
    @Transactional
    public UserResponseDto createUserAndPublishEvent(UserCreateDto dto) {
        log.info("Creating user and publishing event for email: {}", dto.email());
        UserResponseDto createdUser = userService.createUser(dto);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("kafkaCircuitBreaker");
        circuitBreaker.run(() -> {
            kafkaProducer.sendUserAddEvent(new UserEventDto("create", dto.email()));
            return null;
        }, throwable -> {
            log.error("Failed to send Kafka event for user creation: {}", throwable.getMessage());
            return null;
        });

        return createdUser;
    }

    @Override
    @Transactional
    public void deleteUserAndPublishEvent(Long id) {
        log.info("Deleting user and publishing event for user id: {}", id);
        UserResponseDto deletedUser = userService.deleteAndReturnUserById(id);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("kafkaCircuitBreaker");
        circuitBreaker.run(() -> {
            kafkaProducer.sendUserDeleteEvent(new UserEventDto("delete", deletedUser.email()));
            return null;
        }, throwable -> {
            log.error("Failed to send Kafka event for user deletion: {}", throwable.getMessage());
            return null;
        });
    }
}