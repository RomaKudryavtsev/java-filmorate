package ru.yandex.practicum.filmorate.exceptions;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException() {
    }

    public GenreNotFoundException(final String message) {
        super(message);
    }
}
