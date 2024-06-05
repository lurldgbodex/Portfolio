package tech.sgcor.user.exception;

import com.github.dockerjava.api.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.sgcor.sharedservice.dto.CustomError;
import tech.sgcor.sharedservice.exception.ParentExceptionHandler;

@RestControllerAdvice
public class UserExceptionHandler extends ParentExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomError handleInvalidToken(InvalidTokenException ex) {
        return new CustomError(400, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomError handleUserExists(UserExistsException ex) {
        return new CustomError(400, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError handleUserNotFound(UserNotFoundException ex) {
        return new CustomError(404, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomError handleBadRequest(BadRequestException ex) {
        return new CustomError(400, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
