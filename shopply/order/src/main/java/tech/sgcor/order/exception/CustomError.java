package tech.sgcor.order.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Objects;

public record CustomError(
        int code, String error, Map<String, String> errors, HttpStatus status) {
    public CustomError(int code, String error, HttpStatus status) {
        this(code, error, null, status);
        Objects.requireNonNull(error, "error cannot be null");
    }

    public CustomError(int code, Map<String, String> errors, HttpStatus status) {
        this(code, null, errors, status);
        Objects.requireNonNull(errors, "errors");
    }
}
