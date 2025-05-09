package ru.astondevs.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import ru.astondevs.service.UserServiceImpl;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserCreateDto dto) {
        return userServiceImpl.createUser(dto);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userServiceImpl.getUserById(id);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {
        return userServiceImpl.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userServiceImpl.deleteById(id);
    }
}
