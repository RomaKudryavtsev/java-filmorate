package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorFilmDao {
    void addNewFilmDirector(Integer filmId, Integer directorId);

    List<Director> getDirectorsForFilm(Integer filmId);

    void deleteDirectorFilm(Integer filmId, Integer directorId);

    void deleteAllDirectorsForFilm(Integer filmId);

    void deleteAllDirectorLinks(Integer directorId);
}
