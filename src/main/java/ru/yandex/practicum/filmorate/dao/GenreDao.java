package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    List<Genre> getAllGenres();

    List<String> getAllGenreNames();

    Genre getGenreById(Integer genreId);

    Integer getGenreIdForGenreName(String name);

    Integer addNewGenre(String name);
}
