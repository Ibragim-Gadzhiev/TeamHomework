package ru.astondevs.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.repository.UserRepository;
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

        assertThrows(DuplicateEmailException.class, () -> userValidator.validateCreateDto(dto));
    }

    @Test
    void validateUpdateDto_WhenAllFieldsNull_ThrowsException() {
        UserUpdateDto dto = new UserUpdateDto(null, null, null);
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateUpdateDto(dto));
    }

    @Test
    void validateUpdateDto_WhenEmailExist_ThrowsException() {
        UserUpdateDto dto = new UserUpdateDto(null, "unkown.nvme@gmail.com", null);
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userValidator.validateUpdateDto(dto));
    }
}
