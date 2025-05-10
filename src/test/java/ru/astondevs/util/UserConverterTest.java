package ru.astondevs.util;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
