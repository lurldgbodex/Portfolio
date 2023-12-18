package tech.sgcor.portfolio.exceptions;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request
    ) {
       List<String> errors = new ArrayList<String>();
       for(ConstraintViolation<?> violation : ex.getConstraintViolations()) {
           String fieldName = StreamSupport.stream(
                   violation.getPropertyPath().spliterator(), false)
                   .map(Path.Node::getName)
                   .collect(Collectors.joining("."));
           String errorMessage = fieldName.replace("[a-zA-Z]+\\.request\\.", "") + ": " + violation.getMessage();
           errors.add(errorMessage);
       }
       var apiError = new ValidationError(
               HttpStatus.BAD_REQUEST, "An error occurred", errors
       );
       return ResponseEntity.badRequest().body(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info(ex.getClass().getName());

        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();

            String errorMessage = fieldName + ": " + error.getDefaultMessage();
            errors.add(errorMessage);
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        var apiError = new ValidationError(
                HttpStatus.BAD_REQUEST, ex.getDetailMessageCode(), errors
        );
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<CustomError> handleResourceNotFoundException(ResourceNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomError(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND)
                );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomError> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity
                .badRequest()
                .body(new CustomError(HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(), HttpStatus.BAD_REQUEST)
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> handleControllerExceptions(HttpServletRequest request, Throwable ex) {
        logger.error("Error in {} method. Reason: {}", ex.getClass(), ex.getMessage());

        HttpStatus status = getStatus(request);
        logger.error(status.toString());
        return ResponseEntity
                .status(status)
                .body(new CustomError(status.value(), ex.getMessage(), status));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.resolve(code);
        return (status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
