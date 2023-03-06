package ru.yandex.practicum.filmorate.exceptions;

public class ReviewDoesNotExistException extends RuntimeException {
    public ReviewDoesNotExistException() {
    }

    public ReviewDoesNotExistException(final String message) {
        super(message);
    }
}
