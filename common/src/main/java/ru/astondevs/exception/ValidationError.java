package ru.astondevs.exception;

public record ValidationError(String field, String errors) {
}