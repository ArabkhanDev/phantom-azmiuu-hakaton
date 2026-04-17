package az.microservices.azmiuuhakaton.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handle(UsernameNotFoundException exception){
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(Exception exception){
        return new ErrorResponse(exception.getMessage());
    }
}
