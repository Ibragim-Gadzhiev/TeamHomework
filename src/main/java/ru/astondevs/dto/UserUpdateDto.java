package ru.astondevs.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserUpdateDto(
        @Nullable
        @Size(min = 2, max = 50, message = "{name.incorrect}")
        String name,

        @Nullable
        @Email(message = "{email.incorrect}")
        String email,

        @Nullable
        @Min(value = 0, message = "{min.age}") @Max(value = 120, message = "{max.age}")
        Integer age
) {
    public UserUpdateDto {
        if (name == null && email == null && age == null) {
            throw new IllegalArgumentException("Нужно заполнить хотя бы одно поле");
        }
    }
}
