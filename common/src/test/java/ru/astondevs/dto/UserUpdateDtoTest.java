package ru.astondevs.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_ShouldPassValidation() {
        UserUpdateDto dto = new UserUpdateDto("Ibra", "unnown.nvme@gmail.com", 30);
        var violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void invalidUserUpdateDto_ShouldFailValidation() {
        UserUpdateDto dto = new UserUpdateDto("", "👨🏻‍🦽invalid-email", -42);
        var violations = validator.validate(dto);
        assertThat(violations).hasSize(3);
    }
}