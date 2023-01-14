package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    //NOTE: FilmService is dependent from FilmStorage
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(int filmId, int userId) {
        filmStorage.getFilm(filmId).addLike(userId);
    }

    public void cancelLike(int filmId, int userId) {
        filmStorage.getFilm(filmId).removeLike(userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getAllFilms().stream().sorted((f1, f2) -> {
            return -1 * (f1.getAmountOfLikes() - f2.getAmountOfLikes());
        }).limit(count).collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        Film addedFilm = filmStorage.addFilm(film);
        return addedFilm;
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        return film;
    }
}
