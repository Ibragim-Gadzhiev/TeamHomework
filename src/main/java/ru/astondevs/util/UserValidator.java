package ru.astondevs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateCreateDto(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException("Email уже существует");
        }
    }

    public void validateUpdateDto(UserUpdateDto dto) {
        if (dto.name() == null && dto.email() == null && dto.age() == null) {
            throw new IllegalArgumentException("Нужно заполнить хотя бы одно поле");
        }
        if (dto.email() != null && userRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException("Email уже существует");
        }
    }
}
