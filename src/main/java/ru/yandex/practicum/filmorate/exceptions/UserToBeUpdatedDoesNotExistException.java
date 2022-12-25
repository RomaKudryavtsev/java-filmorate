package ru.yandex.practicum.filmorate.exceptions;

public class UserToBeUpdatedDoesNotExistException extends RuntimeException {
    public UserToBeUpdatedDoesNotExistException() {
    }

    public UserToBeUpdatedDoesNotExistException(final String message) {
        super(message);
    }
}
