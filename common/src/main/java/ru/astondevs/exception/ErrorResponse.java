package ru.astondevs.exception;

import java.time.Instant;

public record ErrorResponse(
        String message,
        int status,
        String error,
        String path,
        Instant timestamp
) {
}