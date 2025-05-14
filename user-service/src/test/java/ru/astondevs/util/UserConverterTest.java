package ru.astondevs.util;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserConverterTest {
    private final UserConverter userConverter = new UserConverter();

    @Test
    void toEntity_FromUserCreateDto_ReturnsCorrectEntity() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        User user = userConverter.toEntity(dto);

        assertEquals("Ibra", user.getName());
        assertEquals("unknown.nvme@gmail.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void toEntity_WithMinAge_ReturnsCorrectEntity() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 0);
        User user = userConverter.toEntity(dto);
        assertEquals(0, user.getAge());
    }

    @Test
    void toEntity_WithMaxAge_ReturnsCorrectEntity() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 120);
        User user = userConverter.toEntity(dto);
        assertEquals(120, user.getAge());
    }

    @Test
    void toEntity_WithNullDto_ThrowsException() {
        assertThrows(NullPointerException.class, () -> userConverter.toEntity(null));
    }

    @Test
    void toResponseDto_FromUser_ReturnsCorrectDto() {
        User user = User.builder()
                .id(1L)
                .name("Ibra")
                .email("unknown.nvme@gmail.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        UserResponseDto dto = userConverter.toResponseDto(user);

        assertEquals(1L, dto.id());
        assertEquals("Ibra", dto.name());
        assertEquals("unknown.nvme@gmail.com", dto.email());
        assertEquals(25, dto.age());
        assertNotNull(dto.createdAt());
    }

    @Test
    void toResponseDto_WithNullFields_ReturnsDtoWithDefaults() {
        User user = User.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .build();
        UserResponseDto dto = userConverter.toResponseDto(user);

        assertEquals(1L, dto.id());
        assertNull(dto.name());
        assertNull(dto.email());
        assertNull(dto.age());
    }

    @Test
    void updateEntity_WithUserUpdateDto_UpdatesFields() {
        User user = User.builder()
                .name("IbraVibra")
                .email("gadzhiev.ibragim.for.spam@yandex.ru")
                .age(20)
                .build();
        UserUpdateDto dto = new UserUpdateDto("Ibra", "unknown.nvme@gmail.com", 25);
        userConverter.updateEntity(user, dto);

        assertEquals("Ibra", user.getName());
        assertEquals("unknown.nvme@gmail.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void updateEntity_WithNullFields_DoesNotUpdate() {
        User user = User.builder()
                .name("IbraVibra")
                .email("gadzhiev.ibragim.for.spam@yandex.ru")
                .age(20)
                .build();

        UserUpdateDto dto = new UserUpdateDto(null, null, null);
        userConverter.updateEntity(user, dto);

        assertEquals("IbraVibra", user.getName());
        assertEquals("gadzhiev.ibragim.for.spam@yandex.ru", user.getEmail());
        assertEquals(20, user.getAge());
    }

    @Test
    void updateEntity_UpdateOnlyName_UpdatesName() {
        User user = User.builder()
                .name("IbraVibra")
                .email("gadzhiev.ibragim.for.spam@yandex.ru")
                .age(25)
                .build();
        UserUpdateDto dto = new UserUpdateDto("Ibra", null, null);
        userConverter.updateEntity(user, dto);

        assertEquals("Ibra", user.getName());
        assertEquals("gadzhiev.ibragim.for.spam@yandex.ru", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void updateEntity_UpdateOnlyEmail_UpdatesEmail() {
        User user = User.builder()
                .name("Ibra")
                .email("gadzhiev.ibragim.for.spam@yandex.ru")
                .age(25)
                .build();
        UserUpdateDto dto = new UserUpdateDto(null, "unknown.nvme@gmail.com", null);
        userConverter.updateEntity(user, dto);

        assertEquals("Ibra", user.getName());
        assertEquals("unknown.nvme@gmail.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void updateEntity_UpdateOnlyAge_UpdatesAge() {
        User user = User.builder()
                .name("Ibra")
                .email("unknown.nvme@gmail.com")
                .age(25)
                .build();
        UserUpdateDto dto = new UserUpdateDto(null, null, 25);
        userConverter.updateEntity(user, dto);

        assertEquals("Ibra", user.getName());
        assertEquals("unknown.nvme@gmail.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void updateEntity_UpdateNameAndAge_UpdatesFields() {
        User user = User.builder()
                .name("IbraVibra")
                .email("gadzhiev.ibragim.for.spam@yandex.ru")
                .age(20)
                .build();
        UserUpdateDto dto = new UserUpdateDto("Ibra", null, 25);
        userConverter.updateEntity(user, dto);

        assertEquals("Ibra", user.getName());
        assertEquals("gadzhiev.ibragim.for.spam@yandex.ru", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void updateEntity_DoesNotModifyCreatedAt() {
        LocalDateTime originalDate = LocalDateTime.now().minusDays(1);
        User user = User.builder()
                .name("IbraVibra")
                .email("unknown.nvme@gmail.com")
                .age(25)
                .createdAt(originalDate)
                .build();
        UserUpdateDto dto = new UserUpdateDto("Ibra", null, null);
        userConverter.updateEntity(user, dto);

        assertEquals(originalDate, user.getCreatedAt());
    }

    @Test
    void updateEntity_WithNullDto_ThrowsException() {
        User user = User.builder().build();
        assertThrows(NullPointerException.class, () -> userConverter.updateEntity(user, null));
    }

    @Test
    void updateEntity_UpdateEmailAndAge_UpdatesFields() {
        User user = User.builder()
                .name("Ibra")
                .email("gadzhiev.ibragim.for.spam@yandex.ru")
                .age(20)
                .build();
        UserUpdateDto dto = new UserUpdateDto(null, "unknown.nvme@gmail.com", 25);
        userConverter.updateEntity(user, dto);

        assertEquals("unknown.nvme@gmail.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void updateEntity_UpdateAllFields_UpdatesAll() {
        User user = User.builder()
                .name("IbraVibra")
                .email("gadzhiev.ibragim.for.spam@yandex.ru")
                .age(20)
                .build();
        UserUpdateDto dto = new UserUpdateDto("Ibra", "unknown.nvme@gmail.com", 25);
        userConverter.updateEntity(user, dto);

        assertEquals("Ibra", user.getName());
        assertEquals("unknown.nvme@gmail.com", user.getEmail());
        assertEquals(25, user.getAge());
    }
}
