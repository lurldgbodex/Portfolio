package tech.sgcor.review.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.sgcor.review.dto.CustomError;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CustomError invalidArgumentHandler(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return new CustomError(400, errors);
    }
}
