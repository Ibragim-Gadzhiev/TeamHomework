package ru.astondevs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.util.UserConverter;
import ru.astondevs.util.UserValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ValidDto_ReturnsResponseDto() {
        UserCreateDto createDto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        User user = User.builder().name("Ibra").email("unknown.nvme@gmail.com").age(25).build();
        User savedUser = User.builder().id(1L).build();
        UserResponseDto responseDto = new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now());

        when(userConverter.toEntity(createDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userConverter.toResponseDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.createUser(createDto);

        assertEquals(1L, result.id());
        verify(userValidator).validateCreateDto(createDto);
    }

    @Test
    void getUserById_ExistingUser_ReturnsResponseDto() {
        User user = User.builder().id(1L).build();

        UserResponseDto responseDto = new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userConverter.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(1L);

        assertEquals(1L, result.id());
    }

    @Test
    void updateUser_ValidDto_ReturnsUpdateDto() {
        UserUpdateDto updateDto = new UserUpdateDto("Ibra", "unknown.nvme@gmail.com", 25);
        User user = User.builder().id(1L).name("IbraVibra").build();
        User updateUser = User.builder().id(1L).name("Ibra").build();
        UserResponseDto responseDto = new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updateUser);
        when(userConverter.toResponseDto(updateUser)).thenReturn(responseDto);

        UserResponseDto result = userService.updateUser(1L, updateDto);

        assertEquals("Ibra", result.name());
        verify(userValidator).validateUpdateDto(updateDto);
    }

    @Test
    void deleteById_ExistingUser_DeleteSuccessfully() {
        User user = User.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository).delete(user);
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void deleteById_NonExistingUser_ThrowsException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(1L));
    }
}
