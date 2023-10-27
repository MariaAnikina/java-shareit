package ru.practicum.shareit.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String error = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        log.error(error);
        return new ErrorResponse(error);
    }

    @ExceptionHandler({UserDoesNotExistException.class,ValidationException.class, ItemUnavailableException.class,
            BookingTimeException.class, BookingStateException.class, BookingStatusException.class,
            IllegalArgumentException.class, CommentaryEmptyException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(RuntimeException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, BookingNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(RuntimeException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsException(RuntimeException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ItemOwnerException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleWrongItemOwnerException(RuntimeException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleInternalError(Throwable e) {
//        log.error(e.getMessage());
//        return new ErrorResponse("Произошла непредвиденная ошибка.");
//    }

    @Data
    public static class ErrorResponse {
        private final String error;
    }

//    @ExceptionHandler ({ValidationException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidationError(final ValidationException e) {
//        return new ErrorResponse("Ошибка валидации.");
//    }
//
//    @ExceptionHandler({UserAlreadyExistsException.class})
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse handleValidationError(final RuntimeException e) {
//        return new ErrorResponse("Объект уже существует.");
//    }
//
//    @ExceptionHandler({NullPointerException.class, UpdateNotYoursItemException.class, UserNotFoundException.class})
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleEmptyObjectPassingError(final RuntimeException e) {
//        return new ErrorResponse("Передан пустой объект.");
//    }
//
////    @ExceptionHandler
////    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
////    public ErrorResponse handleThrowable(final Throwable e) {
////        return new ErrorResponse("Произошла непредвиденная ошибка.");
////    }
//
//
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        String error = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
//        log.error(error);
//        return new ErrorResponse(error);
//    }
//
//
//    @Data
//    public static class ErrorResponse {
//        private final String error;
//    }
}
