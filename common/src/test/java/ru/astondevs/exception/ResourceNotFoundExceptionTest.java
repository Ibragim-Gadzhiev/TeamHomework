package ru.astondevs.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void exceptionMessage_ShouldBeCorrect() {
        String message = "Пользователь не найден";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }
}