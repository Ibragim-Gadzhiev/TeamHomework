package ru.astondevs.util;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateCreateDto_WhenEmailExists_ThrowsException() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> userValidator.validateCreateDto(dto)
        );
        assertEquals("Email уже существует", exception.getMessage());
    }

    @Test
    void validateCreateDto_WithNewEmail_DoesNotThrow() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", 25);
        when(userRepository.existsByEmail("unknown.nvme@gmail.com")).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateCreateDto(dto));
    }

    @Test
    void validateCreateDto_WithNullDto_ThrowsException() {
        assertThrows(NullPointerException.class, () -> userValidator.validateCreateDto(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 120})
    void validateCreateDto_ValidAgeBoundaries_DoesNotThrow(int age) {
        UserCreateDto dto = new UserCreateDto("Ibra", "unknown.nvme@gmail.com", age);
        when(userRepository.existsByEmail("unknown.nvme@gmail.com")).thenReturn(false);
        assertDoesNotThrow(() -> userValidator.validateCreateDto(dto));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUpdateDto")
    void validateUpdateDto_InvalidCases_ThrowException(UserUpdateDto dto, Class<? extends Exception> exceptionClass, String message) {
        if (dto.email() != null) {
            when(userRepository.existsByEmail(dto.email())).thenReturn(true);
        }

        Exception exception = assertThrows(exceptionClass, () -> userValidator.validateUpdateDto(dto));
        assertEquals(message, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideValidUpdateDto")
    void validateUpdateDto_ValidCases_DoesNotThrow(UserUpdateDto dto) {
        if (dto.email() != null) {
            when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        }

        assertDoesNotThrow(() -> userValidator.validateUpdateDto(dto));
    }

    @Test
    void validateUpdateDto_NewEmail_DoesNotThrow() {
        UserUpdateDto dto = new UserUpdateDto(null, "unknown.nvme@gmail.com", null);
        when(userRepository.existsByEmail("unknown.nvme@gmail.com")).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateUpdateDto(dto));
    }

    @Test
    void validateUpdateDto_OnlyAge_Valid() {
        UserUpdateDto dto = new UserUpdateDto(null, null, 25);
        assertDoesNotThrow(() -> userValidator.validateUpdateDto(dto));
    }

    private static Stream<Arguments> provideInvalidUpdateDto() {
        return Stream.of(
                Arguments.of(
                        new UserUpdateDto(null, null, null),
                        IllegalArgumentException.class,
                        "Нужно заполнить хотя бы одно поле"
                ),
                Arguments.of(
                        new UserUpdateDto(null, "unknown.nvme@gmail.com", null),
                        DuplicateEmailException.class,
                        "Email уже существует"
                )
        );
    }

    private static Stream<Arguments> provideValidUpdateDto() {
        return Stream.of(
                Arguments.of(new UserUpdateDto("Ibra", null, null)),
                Arguments.of(new UserUpdateDto(null, "unknown.nvme@gmail.com", null)),
                Arguments.of(new UserUpdateDto(null, null, 25)),
                Arguments.of(new UserUpdateDto("Ibra", "gadzhiev.ibragim.for.spam@yandex.ru", 25)),
                Arguments.of(new UserUpdateDto("Ibra", "unknown.nvme@gmail.com", 25))
        );
    }
}
