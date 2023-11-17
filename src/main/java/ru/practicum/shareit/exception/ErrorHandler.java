package ru.practicum.shareit.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String error = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        return new ErrorResponse(error);
    }

    @ExceptionHandler({ValidationException.class, ItemUnavailableException.class,
            BookingTimeException.class, BookingStateException.class, BookingStatusException.class,
            IllegalArgumentException.class, CommentaryEmptyException.class, ItemRequestExistsException.class,
            ItemNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, BookingNotFoundException.class,
            ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsException(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ItemOwnerException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleWrongItemOwnerException(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }

    @Data
    public static class ErrorResponse {
        private final String error;
    }
}
