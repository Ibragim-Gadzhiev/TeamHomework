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

/**
 * REST-контроллер для управления пользователями.
 *
 * <p>Предоставляет CRUD-операции через REST API:
 * <ul>
 *     <li>Создание пользователя</li>
 *     <li>Получение пользователя/списка пользователей</li>
 *     <li>Частичное обновление данных пользователя</li>
 *     <li>Удаление пользователя</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Создание нового пользователя.
     *
     * @param dto DTO с данными нового пользователя
     * @return DTO с созданным пользователем и статусом 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserCreateDto dto) {
        log.info("POST /api/users: Creating user with email '{}'", dto.email());
        UserResponseDto createdUser = userService.createUser(dto);
        log.debug("Created user with ID: {}", createdUser.id());
        return createdUser;
    }

    /**
     * Получение пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return DTO с данными пользователя и статусом 200 (OK)
     * @throws ru.astondevs.exception.ResourceNotFoundException если пользователь не найден
     */
    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{}: Fetching user", id);
        UserResponseDto user = userService.getUserById(id);
        log.debug("Found user: {}", user);
        return user;
    }

    /**
     * Получение списка всех пользователей.
     *
     * @return список DTO пользователей и статусом 200 (OK)
     */
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        log.info("GET /api/users: Fetching all users");
        List<UserResponseDto> users = userService.getAllUsers();
        log.debug("Found {} users", users.size());
        return users;
    }

    /**
     * Обновление данных пользователя.
     *
     * @param id идентификатор пользователя
     * @param dto DTO с обновлёнными данными
     * @return DTO с обновлёнными данными пользователя и статусом 200 (OK)
     * @throws ru.astondevs.exception.ResourceNotFoundException если пользователь не найден
     * @throws ru.astondevs.exception.DuplicateEmailException если новый email уже существует
     */
    @PatchMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {
        log.info("PATCH /api/users/{}: Updating user with data: {}", id, dto);
        UserResponseDto updatedUser = userService.updateUser(id, dto);
        log.debug("Updated user {}: {}", id, updatedUser);
        return updatedUser;
    }

    /**
     * Удаление пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return статус 204 (No Content)
     * @throws ru.astondevs.exception.ResourceNotFoundException если пользователь не найден
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{}: Deleting user", id);
        userService.deleteById(id);
        log.debug("User {} successfully deleted", id);
    }
}
