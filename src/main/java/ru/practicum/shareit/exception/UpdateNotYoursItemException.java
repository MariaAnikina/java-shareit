package ru.practicum.shareit.exception;

public class UpdateNotYoursItemException extends RuntimeException {
    public UpdateNotYoursItemException(String message) {
        super(message);
    }
}
