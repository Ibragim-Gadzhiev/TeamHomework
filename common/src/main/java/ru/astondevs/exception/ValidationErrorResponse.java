package ru.astondevs.exception;

import java.util.List;

public record ValidationErrorResponse(
        String message,
        List<ValidationError> errors
) {}