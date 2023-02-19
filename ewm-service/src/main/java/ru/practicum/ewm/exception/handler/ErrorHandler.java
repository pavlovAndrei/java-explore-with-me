package ru.practicum.ewm.exception.handler;

import static java.time.LocalDateTime.now;

import javax.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.exception.model.ApiError;
import ru.practicum.ewm.exception.model.ConflictException;
import ru.practicum.ewm.exception.model.ForbiddenException;
import ru.practicum.ewm.exception.model.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<Object> handleNotValidMethodArgument(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return buildResponseEntity(new ApiError(e.getMessage(),
                "Incorrectly made request.", BAD_REQUEST, now()));
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn(e.getMessage());
        return buildResponseEntity(new ApiError(e.getMessage(),
                "Integrity constraint has been violated.", BAD_REQUEST, now()));
    }

    @ExceptionHandler
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<Object> handleForbiddenException(final ForbiddenException e) {
        log.warn(e.getMessage());
        return buildResponseEntity(new ApiError(e.getMessage(),
                "This Action is restricted by application.", FORBIDDEN, now()));
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(final NotFoundException e) {
        log.warn(e.getMessage());
        return buildResponseEntity(new ApiError(e.getMessage(),
                "The required object was not found.", NOT_FOUND, now()));
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ResponseEntity<Object> handleConflictException(final ConflictException e) {
        log.warn(e.getMessage());
        return buildResponseEntity(new ApiError(e.getMessage(),
                "For the requested operation the conditions are not met.", CONFLICT, now()));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
