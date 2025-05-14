package ru.astondevs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserCreateDto(
        @NotBlank(message = "{name.not.found}")
        @Size(min = 2, max = 50, message = "{name.incorrect}")
        String name,

        @NotBlank(message = "{email.not.found}")
        @Email(message = "{email.incorrect}")
        String email,

        @Min(value = 0, message = "{min.age}") @Max(value = 120, message = "{max.age}")
        Integer age
) {
}
