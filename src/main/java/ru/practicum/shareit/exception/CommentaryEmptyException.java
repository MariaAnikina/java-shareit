package ru.practicum.shareit.exception;

public class CommentaryEmptyException extends RuntimeException{
	public CommentaryEmptyException(String message) {
		super(message);
	}
}
