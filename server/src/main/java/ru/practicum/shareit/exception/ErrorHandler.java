package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final EntityNotExistException e) {
        return Map.of(
                "error", "Not found",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleOperationNotAllowedException(final OperationNotAllowed e) {
        return Map.of(
                "error", "Not allowed",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleAlreadyExistException(final EntityAlreadyExistException e) {
        return Map.of(
                "error", "Already exist",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class, MissingRequestHeaderException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotValidException(Exception e) {
        return Map.of(
                "error", "Not valid data",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(Throwable e) {
        e.printStackTrace();
        return Map.of(
                "error", "InternalServerError",
                "errorMessage", "Произошла непредвиденная ошибка"
        );
    }
}