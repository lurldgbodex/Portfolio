package tech.sgcor.portfolio.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler{

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ValidationError handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        logger.info(ex.getClass().getName());

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ValidationError(
                HttpStatus.BAD_REQUEST, "Validation failed for request fields", errors
        );
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<CustomError> handleResourceNotFoundException(ResourceNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomError(404, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomError> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity
                .badRequest()
                .body(new CustomError(400, ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomError> handleNotReadable(HttpMessageNotReadableException ex) {
        String errorMessage = "oga check wetin you dey send if e correct";
        return ResponseEntity
                .badRequest()
                .body(new CustomError(400, errorMessage, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CustomError> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.
                status(HttpStatus.NOT_FOUND)
                .body(new CustomError(404, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomError> handleCustomExceptions(HttpServletRequest request, Throwable ex) {
        logger.error("Error in {} method. Reason: {}", ex.getClass(), ex.getMessage());

        HttpStatus status = getStatus(request, ex);
        logger.error(status.toString());

        String errorMessage;
        if (status.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            errorMessage = "Yawa don gas. make una chill as we fix am";
        } else {
            errorMessage = "Na from una end. Check wella wetin u dey input";
        }

        return ResponseEntity
                .status(status)
                .body(new CustomError(status.value(), errorMessage, status));
    }

    private HttpStatus getStatus(HttpServletRequest request, Throwable ex) {
       if (ex instanceof IllegalArgumentException) {
           return HttpStatus.BAD_REQUEST;
       } else if (ex instanceof IllegalStateException) {
           return HttpStatus.CONFLICT;
       }
       return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
