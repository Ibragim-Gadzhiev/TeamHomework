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
import ru.astondevs.config.KafkaConfig;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.exception.ResourceNotFoundException;
import ru.astondevs.service.KafkaProducer;
import ru.astondevs.service.UserService;
import ru.astondevs.service.UserServiceFacade;
import ru.astondevs.service.impl.KafkaProducerImpl;
import ru.astondevs.service.impl.UserServiceFacadeImpl;
import ru.astondevs.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
            return Mockito.mock(UserServiceImpl.class);
        }

        @Bean
        public UserServiceFacade userServiceFacade() {
            return Mockito.mock(UserServiceFacadeImpl.class);
        }

        @Bean
        public KafkaProducer kafkaProducer() {
            return Mockito.mock(KafkaProducerImpl.class);
        }

        @Bean
        public KafkaConfig kafkaConfig() {
            KafkaConfig kafkaConfig = Mockito.mock(KafkaConfig.class);
            when(kafkaConfig.getUserAddTopic()).thenReturn("userAdd-topic");
            when(kafkaConfig.getUserDeleteTopic()).thenReturn("userDelete-topic");
            return kafkaConfig;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceFacade userServiceFacade;

    @Autowired
    private KafkaProducer kafkaProducer;

    private final UserResponseDto testUser = new UserResponseDto(
            1L,
            "Ibra",
            "unknown.nvme@gmail.com",
            25,
            LocalDateTime.now()
    );

    @BeforeEach
    void setUp() {
        Mockito.reset(userService, userServiceFacade, kafkaProducer);
    }

    @Test
    void createUser_ValidRequest_Returns201() throws Exception {
        UserCreateDto createDto = new UserCreateDto("IbraVibra", "gadzhiev.ibragim.for.spam@yandex.ru", 25);
        when(userServiceFacade.createUserAndPublishEvent(any())).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ibra"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.email").value("unknown.nvme@gmail.com"));

        verify(userServiceFacade, times(1)).createUserAndPublishEvent(any(UserCreateDto.class));
    }

    @Test
    void createUser_VerifyAllResponseFields() throws Exception {
        UserCreateDto createDto = new UserCreateDto("Ibra", "gadzhiev.ibragim.for.spam@yandex.ru", 30);
        UserResponseDto response = new UserResponseDto(
                1L, "Ibra", "gadzhiev.ibragim.for.spam@yandex.ru", 30, LocalDateTime.now()
        );

        when(userServiceFacade.createUserAndPublishEvent(any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ibra"))
                .andExpect(jsonPath("$.email").value("gadzhiev.ibragim.for.spam@yandex.ru"))
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
                .andExpect(jsonPath("$.errors[?(@.field == '" + errorField + "')]").exists());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 121})
    void createUser_InvalidAge_Returns400(int age) throws Exception {
        UserCreateDto dto = new UserCreateDto("IbraVibra", "gadzhiev.ibragim.for.spam@yandex.ru", age);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'age')]").exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "NameWithMoreThan50Characters_aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    void createUser_InvalidName_Returns400(String name) throws Exception {
        UserCreateDto dto = new UserCreateDto(name, "gadzhiev.ibragim.for.spam@yandex.ru", 30);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists());
    }

    @Test
    void createUser_DuplicateEmail_Returns409() throws Exception {
        UserCreateDto createDto = new UserCreateDto("Ibra", "duplicate@gmail.com", 30);

        when(userServiceFacade.createUserAndPublishEvent(any()))
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
        UserCreateDto createDto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", age);
        UserResponseDto response = new UserResponseDto(
                1L, "Ibra", "unknown.nvme@gmail.com", age, LocalDateTime.now()
        );

        when(userServiceFacade.createUserAndPublishEvent(createDto)).thenReturn(response);

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
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.name").value("Ibra"));
    }

    @Test
    void getAllUsers_WithResults_ReturnsFullList() throws Exception {
        List<UserResponseDto> users = List.of(
                testUser,
                new UserResponseDto(2L, "IbraVibra", "gadzhive.ibragim.for.spam@yandex.ru", 25, LocalDateTime.now())
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].email").value("gadzhive.ibragim.for.spam@yandex.ru"));
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
                1L, "Ibragim Gadzhiev", "unknown.nvme@gmail.com", 25, testUser.createdAt()
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
                .andExpect(jsonPath("$.errors[?(@.field == '" + errorField + "')]").exists());
    }

    @Test
    void updateUser_EmptyBody_Returns400() throws Exception {
        when(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                .thenThrow(new IllegalArgumentException("Нужно заполнить хотя бы одно поле"));

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Нужно заполнить хотя бы одно поле"));
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

        verify(userServiceFacade, times(1)).deleteUserAndPublishEvent(1L);
    }

    @Test
    void deleteUser_NonExistingUser_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Пользователь не найден"))
                .when(userServiceFacade).deleteUserAndPublishEvent(999L);

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