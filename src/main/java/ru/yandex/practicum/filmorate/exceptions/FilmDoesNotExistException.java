package ru.yandex.practicum.filmorate.exceptions;

public class FilmDoesNotExistException extends RuntimeException {
    public FilmDoesNotExistException() {
    }

    public FilmDoesNotExistException(final String message) {
        super(message);
    }
}
