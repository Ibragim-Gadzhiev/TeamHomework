package ru.astondevs.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateEmailExceptionTest {

    @Test
    void exceptionMessage_ShouldBeCorrect() {
        String message = "Email уже используется";
        DuplicateEmailException exception = new DuplicateEmailException("Email уже используется");

        assertThat(exception.getMessage()).isEqualTo(message);
    }
}