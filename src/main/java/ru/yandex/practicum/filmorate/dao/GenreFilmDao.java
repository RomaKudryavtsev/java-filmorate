package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreFilmDao {
    void addNewGenreFilm(Integer filmId, Integer genreId);
    List<Genre> getGenresForFilm(Integer filmId);
    void deleteGenreFilm(Integer filmId, Integer genreId);
    void deleteGenreFilmLinksForFilm(Integer filmId);
}
