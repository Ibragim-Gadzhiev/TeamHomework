package ru.astondevs.util;

import org.springframework.stereotype.Component;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;
import ru.astondevs.entity.User;

@Component
public class UserConverter {
    public User toEntity(UserCreateDto dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .age(dto.age())
                .build();
    }

    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public void updateEntity(User user, UserUpdateDto dto) {
        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        if (dto.age() != null) {
            user.setAge(dto.age());
        }
    }
}
