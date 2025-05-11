package ru.astondevs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.util.UserConverter;
import ru.astondevs.util.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
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
        assertEquals("unknown.nvme@gmail.com", result.email());
        assertEquals(25, result.age());
        assertNotNull(result.createdAt());
        verify(userValidator).validateCreateDto(createDto);
    }

    @Test
    void createUser_SetsCreatedAt() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        User user = User.builder().build();
        User savedUser = User.builder().id(1L).build();

        when(userConverter.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userConverter.toResponseDto(savedUser)).thenAnswer(inv ->
                new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now())
        );

        UserResponseDto result = userService.createUser(dto);

        assertNotNull(result.createdAt());
    }

    @Test
    void createUser_InvalidDto_ThrowsException() {
        UserCreateDto invalidDto = new UserCreateDto("Ibra", "👨🏻‍🦽invalid-email", 0);

        doThrow(IllegalArgumentException.class)
                .when(userValidator)
                .validateCreateDto(invalidDto);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(invalidDto)
        );

        verify(userConverter, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ExistingUser_ReturnsResponseDto() {
        User user = User.builder().id(1L).build();

        UserResponseDto responseDto = new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userConverter.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(1L);

        assertEquals(1L, result.id());
        assertEquals("unknown.nvme@gmail.com", result.email());
        assertEquals(25, result.age());
        assertNotNull(result.createdAt());
    }

    @Test
    void getUserById_NonExistingUser_ThrowsException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(userId)
        );

        verify(userConverter, never()).toResponseDto(any());
    }

    @Test
    void getAllUsers_ReturnsListOfResponseDto() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        UserResponseDto dto1 = new UserResponseDto(
                1L,
                "Ibra",
                "unknown.nvme@gmail.com",
                25,
                LocalDateTime.now()
        );
        UserResponseDto dto2 = new UserResponseDto(
                2L,
                "IbraVibra",
                "gadzhiev.ibragim.for.spam@yandex.ru",
                20,
                LocalDateTime.now()
        );

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userConverter.toResponseDto(user1)).thenReturn(dto1);
        when(userConverter.toResponseDto(user2)).thenReturn(dto2);

        List<UserResponseDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void getAllUsers_EmptyList_ReturnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());
        List<UserResponseDto> result = userService.getAllUsers();
        assertTrue(result.isEmpty());
    }

    @Test
    void updateUser_ValidDto_ReturnsUpdateDto() {
        UserUpdateDto updateDto = new UserUpdateDto("Ibra", "unknown.nvme@gmail.com", 25);
        User user = User.builder().id(1L).name("IbraVibra").email("gadzhiev.ibragim.for.spam@yandex.ru").age(20).build();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        doAnswer(inv -> {
            User u = inv.getArgument(0);
            UserUpdateDto dto = inv.getArgument(1);
            if (dto.name() != null) u.setName(dto.name());
            if (dto.email() != null) u.setEmail(dto.email());
            if (dto.age() != null) u.setAge(dto.age());
            return null;
        }).when(userConverter).updateEntity(any(User.class), any(UserUpdateDto.class));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userConverter.toResponseDto(userCaptor.capture())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new UserResponseDto(u.getId(), u.getName(), u.getEmail(), u.getAge(), u.getCreatedAt());
        });

        UserResponseDto result = userService.updateUser(1L, updateDto);

        assertEquals("Ibra", result.name());
        assertEquals("unknown.nvme@gmail.com", result.email());
        assertEquals(25, result.age());

        User updatedUser = userCaptor.getValue();
        assertEquals("Ibra", updatedUser.getName());
        assertEquals("unknown.nvme@gmail.com", updatedUser.getEmail());
        assertEquals(25, updatedUser.getAge());

        verify(userValidator).validateUpdateDto(updateDto);
    }

    @Test
    void updateUser_InvalidDto_NoFieldsToUpdate_ThrowsException() {
        Long userId = 1L;
        UserUpdateDto invalidDto = new UserUpdateDto(null, null, null);

        doThrow(IllegalArgumentException.class)
                .when(userValidator)
                .validateUpdateDto(invalidDto);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(userId, invalidDto)
        );

        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_InvalidDto_DuplicateEmail_ThrowsException() {
        Long userId = 1L;
        UserUpdateDto invalidDto = new UserUpdateDto("Ibra", "unknown.nvme@gmail.com", 25);

        doThrow(DuplicateEmailException.class)
                .when(userValidator)
                .validateUpdateDto(invalidDto);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.updateUser(userId, invalidDto)
        );

        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ValidPartialUpdate_Success() {
        Long userId = 1L;
        UserUpdateDto dto = new UserUpdateDto("Ibra", null, null);
        User existingUser = User.builder()
                .id(userId)
                .name("IbraVibra")
                .email("gadzhiev.ibragim.for.spam@yandex.ru")
                .age(20)
                .build();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        doAnswer(inv -> {
            User u = inv.getArgument(0);
            UserUpdateDto updateDto = inv.getArgument(1);
            if (updateDto.name() != null) u.setName(updateDto.name());
            return null;
        }).when(userConverter).updateEntity(any(User.class), any(UserUpdateDto.class));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userConverter.toResponseDto(userCaptor.capture())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new UserResponseDto(u.getId(), u.getName(), u.getEmail(), u.getAge(), u.getCreatedAt());
        });

        UserResponseDto result = userService.updateUser(userId, dto);

        assertEquals("Ibra", result.name());
        assertEquals("gadzhiev.ibragim.for.spam@yandex.ru", result.email());
        assertEquals(20, result.age());

        User updatedUser = userCaptor.getValue();
        assertEquals("Ibra", updatedUser.getName());
        verify(userValidator).validateUpdateDto(dto);
    }

    @Test
    void updateUser_DoesNotChangeCreatedAt() {
        LocalDateTime originalDate = LocalDateTime.now().minusDays(1);
        User existingUser = User.builder()
                .id(1L)
                .name("Ibra")
                .email("unknown.nvme@gmail.com")
                .age(25)
                .createdAt(originalDate)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userConverter.toResponseDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserResponseDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getAge(),
                    user.getCreatedAt()
            );
        });

        doAnswer(inv -> {
            User user = inv.getArgument(0);
            UserUpdateDto dto = inv.getArgument(1);
            if (dto.name() != null) user.setName(dto.name());
            return null;
        }).when(userConverter).updateEntity(any(User.class), any(UserUpdateDto.class));

        UserResponseDto result = userService.updateUser(1L, new UserUpdateDto("Ibra", null, null));

        assertEquals(originalDate, result.createdAt());
    }

    @ParameterizedTest
    @MethodSource("updateUserProvider")
    void updateUser_PartialUpdates(
            UserUpdateDto dto,
            String expectedName,
            String expectedEmail,
            Integer expectedAge
    ) {
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .name("Ibra")
                .email("unknown.nvme@gmail.com")
                .age(20)
                .build();

        doAnswer(inv -> {
            User user = inv.getArgument(0);
            UserUpdateDto updateDto = inv.getArgument(1);
            if (updateDto.name() != null) user.setName(updateDto.name());
            if (updateDto.email() != null) user.setEmail(updateDto.email());
            if (updateDto.age() != null) user.setAge(updateDto.age());
            return null;
        }).when(userConverter).updateEntity(any(), any());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userConverter.toResponseDto(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            return new UserResponseDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getAge(),
                    LocalDateTime.now()
            );
        });

        UserResponseDto result = userService.updateUser(userId, dto);

        assertEquals(expectedName, result.name());
        assertEquals(expectedEmail, result.email());
        assertEquals(expectedAge, result.age());

        verify(userConverter).updateEntity(eq(existingUser), eq(dto));
        verify(userValidator).validateUpdateDto(dto);
    }

    private static Stream<Arguments> updateUserProvider() {
        return Stream.of(
                Arguments.of(
                        new UserUpdateDto("IbraVibra", null, null),
                        "IbraVibra",
                        "unknown.nvme@gmail.com",
                        20
                ),

                Arguments.of(
                        new UserUpdateDto(null, "gadzhiev.ibragim.for.spam@yandex.ru", null),
                        "Ibra",
                        "gadzhiev.ibragim.for.spam@yandex.ru",
                        20
                ),

                Arguments.of(
                        new UserUpdateDto(null, null, 25),
                        "Ibra",
                        "unknown.nvme@gmail.com",
                        25
                ),

                Arguments.of(
                        new UserUpdateDto("IbraVibra", null, 25),
                        "IbraVibra",
                        "unknown.nvme@gmail.com",
                        25
                ),

                Arguments.of(
                        new UserUpdateDto("IbraVibra", "unknown.nvme@gmail.com", 25),
                        "IbraVibra",
                        "unknown.nvme@gmail.com",
                        25
                )
        );
    }

    @Test
    void deleteById_NonExistingUser_ThrowsException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(1L));
    }

    @Test
    void deleteById_ExistingUser_Success() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository).delete(user);
    }
}
