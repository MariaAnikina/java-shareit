package ru.practicum.shareit.exception;

public class ItemRequestExistsException extends RuntimeException {
	public ItemRequestExistsException(String s) {
		super(s);
	}
}
