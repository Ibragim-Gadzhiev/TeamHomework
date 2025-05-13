package ru.astondevs.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.exception.DuplicateEmailException;
import ru.astondevs.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateCreateDto(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            log.error("Email already exists: {}", dto.email());
            throw new DuplicateEmailException("Email уже существует");
        }
        if (dto.age() != null && (dto.age() < 0 || dto.age() > 120)) {
            log.error("Invalid age provided: {}", dto.age());
            throw new IllegalArgumentException("Возраст должен быть в пределах от 0 до 120 лет");
        }
    }

    public void validateUpdateDto(UserUpdateDto dto) {
        if (dto.name() == null && dto.email() == null && dto.age() == null) {
            log.warn("No fields provided for update");
            throw new IllegalArgumentException("Нужно заполнить хотя бы одно поле");
        }
        if (dto.email() != null && userRepository.existsByEmail(dto.email())) {
            log.error("Email conflict during update: {}", dto.email());
            throw new DuplicateEmailException("Email уже существует");
        }
    }
}