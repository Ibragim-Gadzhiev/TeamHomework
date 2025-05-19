package ru.astondevs.exception;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.KafkaException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(DuplicateEmailException ex) {
        return buildError(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception ex) {
        return buildError("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<ValidationError>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        return Map.of("errors", errors);
    }

    @ExceptionHandler(KafkaException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleKafkaException(Exception ex) {
        return buildError("Kafka error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMessagingException(Exception ex) {
        return buildError("Email error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse buildError(String message, HttpStatus status) {
        return new ErrorResponse(message, status.getReasonPhrase().toLowerCase());
    }
}