package ru.astondevs.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserCreateDto dto) {
        log.info("POST /api/users: Creating user with email '{}'", dto.email());
        UserResponseDto createdUser = userService.createUser(dto);
        log.debug("Created user with ID: {}", createdUser.id());
        return createdUser;
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{}: Fetching user", id);
        UserResponseDto user = userService.getUserById(id);
        log.debug("Found user: {}", user);
        return user;
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        log.info("GET /api/users: Fetching all users");
        List<UserResponseDto> users = userService.getAllUsers();
        log.debug("Found {} users", users.size());
        return users;
    }

    @PatchMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {
        log.info("PATCH /api/users/{}: Updating user with data: {}", id, dto);
        UserResponseDto updatedUser = userService.updateUser(id, dto);
        log.debug("Updated user {}: {}", id, updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{}: Deleting user", id);
        userService.deleteById(id);
        log.debug("User {} successfully deleted", id);
    }
}
