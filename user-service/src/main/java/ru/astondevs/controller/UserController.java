package ru.astondevs.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserResponseWrapper;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.service.UserService;
import ru.astondevs.service.UserServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Управление пользователями")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceFacade userServiceFacade;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать пользователя",
            description = "Создает нового пользователя и публикует событие",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                            content = @Content(schema = @Schema(implementation = UserResponseWrapper.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
            }
    )
    public ResponseEntity<UserResponseWrapper> createUser(
            @RequestBody(description = "Данные для создания пользователя", required = true,
                    content = @Content(schema = @Schema(implementation = UserCreateDto.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody UserCreateDto dto) {
        UserResponseDto createdUser = userServiceFacade.createUserAndPublishEvent(dto);
        UserResponseWrapper response = UserResponseWrapper.wrap(createdUser);
        return ResponseEntity
                .created(Link.of("/api/users/" + createdUser.id()).toUri())
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя с указанным идентификатором",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос",
                            content = @Content(schema = @Schema(implementation = UserResponseWrapper.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    public ResponseEntity<UserResponseWrapper> getUserById(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        UserResponseDto userDto = userService.getUserById(id);
        UserResponseWrapper response = UserResponseWrapper.wrap(userDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseWrapper.class))))
            }
    )
    public ResponseEntity<CollectionModel<UserResponseWrapper>> getAllUsers() {
        List<UserResponseDto> userDtos = userService.getAllUsers();
        List<UserResponseWrapper> wrappedUsers = UserResponseWrapper.wrapAll(userDtos);
        CollectionModel<UserResponseWrapper> model = CollectionModel.of(wrappedUsers);
        model.add(Link.of("/api/users").withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновить пользователя",
            description = "Частично обновляет данные пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                            content = @Content(schema = @Schema(implementation = UserResponseWrapper.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    public ResponseEntity<UserResponseWrapper> updateUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Данные для обновления пользователя", required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateDto.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody UserUpdateDto dto) {
        UserResponseDto updatedUser = userService.updateUser(id, dto);
        UserResponseWrapper response = UserResponseWrapper.wrap(updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по ID и публикует событие",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален",
                            content = @Content(schema = @Schema(implementation = UserResponseWrapper.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        userServiceFacade.deleteUserAndPublishEvent(id);
        Link usersLink = Link.of("/api/users").withRel("users");
        return ResponseEntity.noContent()
                .header("Link", usersLink.toString())
                .build();
    }
}