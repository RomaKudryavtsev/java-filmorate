package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    //NOTE: FilmService is dependent from FilmStorage
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    private boolean validateFilm(Film film) throws ValidationException {
        if(film.getDescription().length() > 200) {
            throw new ValidationException("Film description may not exceed 200 symbols");
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Invalid film release date");
        }
        if(film.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive number");
        }
        return true;
    }

    boolean checkIfFilmExists(int filmId) {
        Set<Integer> allCurrentFilmIds = filmStorage.getAllFilms().stream().map(Film::getId)
                .collect(Collectors.toSet());
        if(!allCurrentFilmIds.contains(filmId)) {
            throw new FilmDoesNotExistException("Film does not exist");
        }
        return true;
    }

    public void addLike(int filmId, int userId) {
        checkIfFilmExists(filmId);
        userService.checkIfUserExists(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void cancelLike(int filmId, int userId) {
        checkIfFilmExists(filmId);
        userService.checkIfUserExists(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getMostLikedFilms(count);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        if(film.getUsersLiked() == null) {
            film.setUsersLiked(new HashSet<>());
        }
        validateFilm(film);
        if(filmStorage.getAllFilms().stream().map(Film::getId)
                .collect(Collectors.toSet()).contains(film.getId())) {
            throw new FilmAlreadyExistsException("This film already exists");
        }
        Film addedFilm = filmStorage.addFilm(film);
        return addedFilm;
    }

    public Film getFilmById(int filmId) {
        checkIfFilmExists(filmId);
        return filmStorage.getFilm(filmId);
    }

    public void deleteFilmById(int filmId) {
        checkIfFilmExists(filmId);
        filmStorage.deleteFilm(filmId);
    }

    public Film updateFilm(Film film) {
        if(!filmStorage.getAllFilms().stream().map(Film::getId)
                .collect(Collectors.toSet()).contains(film.getId())) {
            throw new FilmToBeUpdatedDoesNotExistException("Film to be updated does not exist");
        }
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int genreId) {
        return filmStorage.getGenreById(genreId);
    }

    public List<Rating> getAllRatings() {
        return filmStorage.getAllRatings();
    }

    public Rating getRatingById(int ratingId) {
        return filmStorage.getRatingById(ratingId);
    }
}
