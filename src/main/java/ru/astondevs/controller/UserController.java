package ru.astondevs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Создание нового пользователя
     * @param dto DTO с данными нового пользователя
     * @return ResponseEntity с созданным пользователем и статусом 201 (Created)
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        try {
            UserResponseDto createdUser = userService.createUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (DuplicateEmailException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Получение пользователя по ID
     * @param id идентификатор пользователя
     * @return ResponseEntity с данными пользователя и статусом 200 (OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        try {
            UserResponseDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получение списка всех пользователей
     * @return ResponseEntity со списком пользователей и статусом 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Обновление данных пользователя
     * @param id идентификатор пользователя
     * @param dto DTO с обновлёнными данными
     * @return ResponseEntity с обновлёнными данными пользователя и статусом 200 (OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {
        try {
            UserResponseDto updatedUser = userService.updateUser(id, dto);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (DuplicateEmailException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Удаление пользователя
     * @param id идентификатор пользователя
     * @return ResponseEntity со статусом 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
