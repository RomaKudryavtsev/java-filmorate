package ru.yandex.practicum.filmorate.exceptions;

public class FilmToBeUpdatedDoesNotExistException extends RuntimeException {
    public FilmToBeUpdatedDoesNotExistException() {
    }

    public FilmToBeUpdatedDoesNotExistException(final String message) {
        super(message);
    }
}
