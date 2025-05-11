package ru.astondevs.integration;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.service.UserServiceImpl;
import java.time.temporal.ChronoUnit;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ShouldPersistAllFields() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        UserResponseDto response = userService.createUser(dto);

        assertThat(response.name()).isEqualTo("Ibra");
        assertThat(response.email()).isEqualTo("unknown.nvme@gmail.com");
        assertThat(response.age()).isEqualTo(25);
    }

    @Test
    void createUser_WithInvalidEmail_ShouldThrow() {
        UserCreateDto dto = new UserCreateDto("Invalid", "not-an-email", 30);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrow() {
        userService.createUser(new UserCreateDto("IbraVibra", "unknown.nvme@gmail.com", 20));
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void createUser_WithMinAge_ShouldSucceed() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 0);
        UserResponseDto response = userService.createUser(dto);
        assertThat(response.age()).isEqualTo(0);
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrow() {
        UserUpdateDto dto = new UserUpdateDto("Ibra", null, null);
        assertThatThrownBy(() -> userService.updateUser(999L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getUserById_ShouldThrowWhenNotFound() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void updateUser_ShouldHandlePartialUpdate() {
        UserCreateDto createDto = new UserCreateDto("IbraVibra", "gadzhiev.ibragim.for.spam@yandex.ru", 20);
        UserResponseDto createdUser = userService.createUser(createDto);

        UserUpdateDto updateDto = new UserUpdateDto(
                "Ibra",
                null,
                25
        );

        UserResponseDto updatedUser = userService.updateUser(createdUser.id(), updateDto);

        assertThat(updatedUser.email()).isEqualTo(createdUser.email());
        assertThat(updatedUser.name()).isEqualTo("Ibra");
        assertThat(updatedUser.email()).isEqualTo("gadzhiev.ibragim.for.spam@yandex.ru");
        assertThat(updatedUser.age()).isEqualTo(25);
    }

    @Test
    void checkValidationMessage_ShouldContainCustomText() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", -5);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Age cannot be negative");
    }

    @Test
    void updateUser_WithInvalidEmail_ShouldThrow() {
        UserResponseDto createdUser = userService.createUser(new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25));
        UserUpdateDto dto = new UserUpdateDto(null, "👨🏻‍🦽invalid-email", null);
        assertThatThrownBy(() -> userService.updateUser(createdUser.id(), dto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void deleteUser_ShouldCascadeCorrectly() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 120);
        UserResponseDto createdUser = userService.createUser(dto);

        userService.deleteById(createdUser.id());

        assertThat(userRepository.existsById(createdUser.id())).isFalse();
    }

    @Test
    void createdAt_ShouldNotChangeAfterUpdate() {
        UserCreateDto dto = new UserCreateDto("IbraVibra", "gadzhiev.ibragim.for.spam@yandex.ru", 20);
        UserResponseDto createdUser = userService.createUser(dto);

        LocalDateTime initialCreatedAt = createdUser.createdAt();

        UserUpdateDto updateDto = new UserUpdateDto("Ibra", null, null);
        userService.updateUser(createdUser.id(), updateDto);

        UserResponseDto updatedUser = userService.getUserById(createdUser.id());

        assertThat(updatedUser.createdAt()).isEqualTo(initialCreatedAt);
    }
}