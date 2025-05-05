package ru.astondevs.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.TestConfig.class)
public class UserControllerTest {
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private final UserResponseDto testUser = new UserResponseDto(
            1L,
            "Test",
            "unknown.nvme@gmail.com",
            30,
            LocalDateTime.now()
    );

    @BeforeEach
    void setUp() {
        Mockito.reset(userService);
    }

    @Test
    void createUser_ValidRequest_Returns201() throws Exception {
        UserCreateDto createDto = new UserCreateDto("Test", "unknown.nvme@gmail.com", 30);
        when(userService.createUser(any())).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("unknown.nvme@gmail.com"));
    }

    @Test
    void createUser_VerifyAllResponseFields() throws Exception {
        UserCreateDto createDto = new UserCreateDto("Ibragim", "gadzhiev.ibragim@yandex.ru", 30);
        UserResponseDto response = new UserResponseDto(
                1L, "Ibragim", "gadzhiev.ibragim@yandex.ru", 30, LocalDateTime.now()
        );

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ibragim"))
                .andExpect(jsonPath("$.email").value("gadzhiev.ibragim@yandex.ru"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCreateUsers")
    void createUser_InvalidRequest_Returns400(UserCreateDto dto, String errorField) throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + errorField).exists());
    }

    @Test
    void createUser_DuplicateEmail_Returns409() throws Exception {
        UserCreateDto createDto = new UserCreateDto("Test", "duplicate@gmail.com", 30);

        when(userService.createUser(any()))
                .thenThrow(new DuplicateEmailException("Email уже используется"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email уже используется"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 120})
    void createUser_ValidAgeBoundaries_Returns201(int age) throws Exception {
        UserCreateDto createDto = new UserCreateDto("Test", "unknown.nvme@gmail.com", age);
        UserResponseDto response = new UserResponseDto(
                1L, "Test", "unknown.nvme@gmail.com", age, LocalDateTime.now()
        );

        when(userService.createUser(createDto)).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    void getUserById_ExistingUser_Returns200() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("unknown.nvme@gmail.com"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void getAllUsers_WithResults_ReturnsFullList() throws Exception {
        List<UserResponseDto> users = List.of(
                testUser,
                new UserResponseDto(2L, "User 2", "user2@gmail.com", 25, LocalDateTime.now())
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].email").value("user2@gmail.com"));
    }

    @Test
    void getAllUsers_EmptyList_ReturnsEmptyArray() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserById_NonExistingUser_Returns404() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new ResourceNotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }

    @Test
    void updateUser_ValidRequest_Returns200() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto("Ibragim Gadzhiev", null, null);
        UserResponseDto updatedUser = new UserResponseDto(
                1L, "Ibragim Gadzhiev", "unknown.nvme@gmail.com", 30, testUser.createdAt()
        );

        when(userService.updateUser(1L, updateDto)).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ibragim Gadzhiev"));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUpdateUsers")
    void updateUser_InvalidRequest_Returns400(UserUpdateDto dto, String errorField) throws Exception {
        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + errorField).exists());
    }

    @Test
    void updateUser_EmptyBody_Returns400() throws Exception {
        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_DuplicateEmail_Returns409() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto(null, "duplicate@gmail.com", null);

        when(userService.updateUser(anyLong(), any()))
                .thenThrow(new DuplicateEmailException("Email уже используется"));

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email уже используется"));
    }

    @Test
    void updateUser_OnlyAge_KeepsOtherFields() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto(null, null, 35);
        UserResponseDto updatedUser = new UserResponseDto(
                1L, testUser.name(), testUser.email(), 35, testUser.createdAt()
        );

        when(userService.updateUser(1L, updateDto)).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(jsonPath("$.name").value(testUser.name()))
                .andExpect(jsonPath("$.email").value(testUser.email()));
    }

    @Test
    void deleteUser_ExistingUser_Returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NonExistingUser_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Пользователь не найден"))
                .when(userService).deleteById(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }

    private static Stream<Arguments> provideInvalidCreateUsers() {
        return Stream.of(
                Arguments.of(new UserCreateDto("A", "valid@email.com", 30), "name"),
                Arguments.of(new UserCreateDto("Valid Name", "invalid-email", 30), "email"),
                Arguments.of(new UserCreateDto("Valid Name", "valid@email.com", -5), "age"),
                Arguments.of(new UserCreateDto("Valid Name", "valid@email.com", 121), "age")
        );
    }

    private static Stream<Arguments> provideInvalidUpdateUsers() {
        return Stream.of(
                Arguments.of(new UserUpdateDto("A", null, null), "name"),
                Arguments.of(new UserUpdateDto(null, "invalid-email", null), "email"),
                Arguments.of(new UserUpdateDto(null, null, 150), "age"),
                Arguments.of(new UserUpdateDto(null, null, -10), "age")
        );
    }
}
