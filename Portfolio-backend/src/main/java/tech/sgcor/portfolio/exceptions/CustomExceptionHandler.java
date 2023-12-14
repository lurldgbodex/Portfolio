package tech.sgcor.portfolio.exceptions;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<CustomError> handleResourceNotFoundException(ResourceNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> handleControllerExceptions(HttpServletRequest request, Throwable ex) {
        logger.error("An unexpected error occurred." + ex.getMessage());

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
