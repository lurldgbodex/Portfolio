package tech.sgcor.portfolio.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
public class ValidationError {

    private final HttpStatus status;
    private final String message;
    private final Map<String, String> errors;

    public ValidationError(HttpStatus status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
