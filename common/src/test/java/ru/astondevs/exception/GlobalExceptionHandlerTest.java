package ru.astondevs.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_ShouldReturnCorrectResponse() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Пользователь не найден");

        ErrorResponse response = exceptionHandler.handleResourceNotFound(exception);

        assertThat(response.message()).isEqualTo("Пользователь не найден");
        assertThat(response.httpStatus()).isEqualTo("not found");
    }

    @Test
    void handleDuplicateEmail_ShouldReturnConflictResponse() {
        DuplicateEmailException exception = new DuplicateEmailException("Email уже существует");

        ErrorResponse response = exceptionHandler.handleDuplicateEmail(exception);

        assertThat(response.message()).isEqualTo("Email уже существует");
        assertThat(response.httpStatus()).isEqualTo("conflict");
    }

    @Test
    void handleIllegalArgument_ShouldReturnBadRequestResponse() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ErrorResponse response = exceptionHandler.handleIllegalArgument(exception);

        assertThat(response.message()).isEqualTo("Invalid argument");
        assertThat(response.httpStatus()).isEqualTo("bad request");
    }

    @Test
    void handleAllExceptions_ShouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception("Unexpected error");

        ErrorResponse response = exceptionHandler.handleAllExceptions(exception);

        assertThat(response.message()).isEqualTo("Internal server error");
        assertThat(response.httpStatus()).isEqualTo("internal server error");
    }

    @Test
    void handleKafkaException_ShouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception("Kafka failure");

        ErrorResponse response = exceptionHandler.handleKafkaException(exception);

        assertThat(response.message()).isEqualTo("Kafka error: Kafka failure");
        assertThat(response.httpStatus()).isEqualTo("internal server error");
    }

    @Test
    void handleMessagingException_ShouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception("Email service failed");

        ErrorResponse response = exceptionHandler.handleMessagingException(exception);

        assertThat(response.message()).isEqualTo("Email error: Email service failed");
        assertThat(response.httpStatus()).isEqualTo("internal server error");
    }
}