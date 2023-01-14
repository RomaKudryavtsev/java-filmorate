package ru.yandex.practicum.filmorate.exceptions;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException() {
    }

    public UserDoesNotExistException(final String message) {
        super(message);
    }
}
