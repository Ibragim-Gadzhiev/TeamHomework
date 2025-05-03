package ru.astondevs.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String message, HttpStatus httpStatus) {
}
