package ru.practicum.shareit.exception;

public class NegativeValueException extends RuntimeException {
	public NegativeValueException(String message) {
		super(message);
	}
}
