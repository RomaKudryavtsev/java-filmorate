package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDao {
    List<Director> getAllDirectors();
    Director getDirectorById(Integer directorId);
    Director addDirector(Director director);
    void deleteDirector(Integer directorId);
    Director updateDirector(Director director);
}
