package tech.sgcor.sharedservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.sgcor.sharedservice.dto.CustomError;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ParentExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomError handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors  = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(field ->
                errors.put(field.getField(), field.getDefaultMessage())
        );

        return new CustomError(400, errors, HttpStatus.BAD_REQUEST);
    }
}
