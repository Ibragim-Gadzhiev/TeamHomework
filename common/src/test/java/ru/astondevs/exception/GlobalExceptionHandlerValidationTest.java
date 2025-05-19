package ru.astondevs.exception;


import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerValidationTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleValidationExceptions_ShouldReturnValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("objectName", "name", "Name is required"),
                new FieldError("objectName", "email", "Email is invalid")
        ));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        Map<String, List<ValidationError>> response = exceptionHandler.handleValidationExceptions(exception);

        assertThat(response).containsKey("errors");
        assertThat(response.get("errors")).hasSize(2);
        assertThat(response.get("errors")).extracting("field").containsExactly("name", "email");
        assertThat(response.get("errors")).extracting("message").containsExactly("Name is required", "Email is invalid");
    }
}
