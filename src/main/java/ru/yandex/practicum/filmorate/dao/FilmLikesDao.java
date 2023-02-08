package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface FilmLikesDao {
    void addLike(Integer filmId, Integer userId);
    void removeLike(Integer filmId, Integer userId);
    Set<Integer> getLikesOfFilm(Integer filmId);
    void deleteAllLikesForFilm(Integer filmId);
    void deleteAllLikesOfUser(Integer userId);
}
