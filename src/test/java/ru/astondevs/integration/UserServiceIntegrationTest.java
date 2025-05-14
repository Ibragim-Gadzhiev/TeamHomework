package ru.astondevs.integration;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.service.KafkaProducer;
import ru.astondevs.service.impl.UserServiceImpl;
import ru.astondevs.util.UserConverter;
import ru.astondevs.util.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceIntegrationTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userRepository, userValidator, userConverter);
    }

    @Test
    void createUser_ShouldPersistUser() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        User user = new User(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now());
        Mockito.when(userConverter.toEntity(dto)).thenReturn(user);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(userConverter.toResponseDto(user)).thenReturn(new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now()));

        UserResponseDto response = userService.createUser(dto);

        assertThat(response.name()).isEqualTo("Ibra");
        assertThat(response.email()).isEqualTo("unknown.nvme@gmail.com");
        assertThat(response.age()).isEqualTo(25);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "👨🏻‍🦽invalid-email", "ibra@vibra"})
    void createUser_WithInvalidEmail_ShouldThrow(String invalidEmail) {
        UserCreateDto dto = new UserCreateDto("Ibra", invalidEmail, 25);

        Mockito.doThrow(new ConstraintViolationException("👨🏻‍🦽invalid-email", null))
                .when(userValidator).validateCreateDto(dto);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("👨🏻‍🦽invalid-email");
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, 0, 150})
    void createUser_WithInvalidAge_ShouldThrow(int invalidAge) {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", invalidAge);

        Mockito.doThrow(new ConstraintViolationException("Invalid age", null))
                .when(userValidator).validateCreateDto(dto);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Invalid age");
    }

    @Test
    void getUserById_ShouldReturnUser() {
        User user = new User(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now());
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userConverter.toResponseDto(user)).thenReturn(new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now()));

        UserResponseDto response = userService.getUserById(1L);

        assertThat(response.name()).isEqualTo("Ibra");
        assertThat(response.email()).isEqualTo("unknown.nvme@gmail.com");
    }

    @Test
    void getUserById_ShouldThrowResourceNotFoundException() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        User user = new User(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now());
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        Mockito.when(userConverter.toResponseDto(user)).thenReturn(new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now()));

        List<UserResponseDto> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).name()).isEqualTo("Ibra");
    }

    @Test
    void updateUser_ShouldUpdateFields() {
        User user = new User(1L, "IbraVibra", "gadzhiev.ibragim.for.spam@yandex.ru", 20, LocalDateTime.now());
        UserUpdateDto updateDto = new UserUpdateDto("Ibra", "unknown.nvme@gmail.com", 25);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userConverter.toResponseDto(user)).thenReturn(new UserResponseDto(1L, "Ibra", "unknown.nvme@gmail.com", 25, user.getCreatedAt()));

        UserResponseDto response = userService.updateUser(1L, updateDto);

        assertThat(response.name()).isEqualTo("Ibra");
        assertThat(response.email()).isEqualTo("unknown.nvme@gmail.com");
        assertThat(response.age()).isEqualTo(25);
    }

    @ParameterizedTest
    @CsvSource({
            "Ibra, , ",
            ", unknown.nvme@gmail.com, ",
            ", , 25",
            "Ibra, unknown.nvme@gmail.com, 25"
    })
    void updateUser_ShouldUpdateFields(String name, String email, Integer age) {
        User user = new User(1L, "IbraVibra", "gadzhiev.ibragim.for.spam@yandex.ru", 20, LocalDateTime.now());
        UserUpdateDto updateDto = new UserUpdateDto(name, email, age);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userConverter.toResponseDto(user))
                .thenReturn(new UserResponseDto(1L, name != null ? name : user.getName(), email != null ? email : user.getEmail(), age != null ? age : user.getAge(), user.getCreatedAt()));

        UserResponseDto response = userService.updateUser(1L, updateDto);

        if (name != null) assertThat(response.name()).isEqualTo(name);
        if (email != null) assertThat(response.email()).isEqualTo(email);
        if (age != null) assertThat(response.age()).isEqualTo(age);
    }

    @Test
    void updateUser_ShouldThrowResourceNotFoundException() {
        UserUpdateDto updateDto = new UserUpdateDto("UpdatedName", "updated.email@gmail.com", 30);

        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1L, 0L})
    void deleteUser_ShouldThrowResourceNotFoundException(Long invalidId) {
        assertThatThrownBy(() -> userService.deleteById(invalidId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() {
        User user = new User(1L, "Ibra", "unknown.nvme@gmail.com", 25, LocalDateTime.now());
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository).delete(user);
    }
}