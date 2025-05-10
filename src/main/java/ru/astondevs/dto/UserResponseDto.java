package ru.astondevs.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String name,
        String email,
        int age,
        LocalDateTime createdAt

) {
}