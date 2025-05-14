package ru.astondevs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.service.impl.UserServiceFacadeImpl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private UserServiceFacadeImpl userServiceFacade;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService, kafkaProducer);
    }

    @Test
    void createUserAndPublishEvent_ShouldSendKafkaEvent() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        UserResponseDto responseDto = new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, null);

        Mockito.when(userService.createUser(dto)).thenReturn(responseDto);

        userServiceFacade.createUserAndPublishEvent(dto);

        verify(userService).createUser(dto);
        verify(kafkaProducer).sendUserAddEvent(Mockito.any());
    }

    @Test
    void deleteUserAndPublishEvent_ShouldSendKafkaEvent() {
        Long userId = 1L;
        UserResponseDto responseDto = new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, null);

        Mockito.when(userService.deleteAndReturnUserById(userId)).thenReturn(responseDto);

        userServiceFacade.deleteUserAndPublishEvent(userId);

        verify(userService).deleteAndReturnUserById(userId);
        verify(kafkaProducer).sendUserDeleteEvent(Mockito.any());
    }

    @Test
    void deleteUserAndPublishEvent_ShouldThrowException_WhenUserNotFound() {
        Long userId = 999L;

        Mockito.when(userService.deleteAndReturnUserById(userId))
                .thenThrow(new ResourceNotFoundException("Пользователь не найден"));

        assertThatThrownBy(() -> userServiceFacade.deleteUserAndPublishEvent(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }
}