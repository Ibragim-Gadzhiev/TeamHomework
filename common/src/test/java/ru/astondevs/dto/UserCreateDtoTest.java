package ru.astondevs.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserCreateDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_ShouldPassValidation() {
        UserCreateDto dto = new UserCreateDto("Ibra", "unnown.nvme@gmail.com", 25);
        var violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void invalidDto_ShouldFailValidation() {
        UserCreateDto dto = new UserCreateDto("", "👨🏻‍🦽invalid-email", -42);
        var violations = validator.validate(dto);
        assertThat(violations).hasSize(4);
    }
}