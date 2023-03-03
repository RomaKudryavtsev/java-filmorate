package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final String SEARCH_CATEGORY_DIRECTOR = "director";
    private static final String SEARCH_CATEGORY_TITLE = "title";

    //NOTE: FilmService is dependent from FilmStorage
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    private boolean validateFilm(Film film) throws ValidationException {
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Film description may not exceed 200 symbols");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Invalid film release date");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive number");
        }
        return true;
    }

    private boolean checkIfFilmExists(Integer filmId) {
        if(filmId != null) {
            Set<Integer> allCurrentFilmIds = filmStorage.getAllFilms().stream().map(Film::getId)
                    .collect(Collectors.toSet());
            if (!allCurrentFilmIds.contains(filmId)) {
                throw new FilmDoesNotExistException("Film does not exist");
            }
            return true;
        }
        return false;
    }

    private boolean checkIfReviewExists(int reviewId) {
        try {
            filmStorage.getReviewById(reviewId);
            return true;
        } catch (ReviewDoesNotExistException e) {
            return false;
        }
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
        if (film.getUsersLiked() == null) {
            film.setUsersLiked(new HashSet<>());
        }
        validateFilm(film);
        if (filmStorage.getAllFilms().stream().map(Film::getId)
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
        if (!filmStorage.getAllFilms().stream().map(Film::getId)
                .collect(Collectors.toSet()).contains(film.getId())) {
            throw new FilmToBeUpdatedDoesNotExistException("Film to be updated does not exist");
        }
        return filmStorage.updateFilm(film);
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

    public List<Director> getAllDirectors() {
        return filmStorage.getAllDirectors();
    }

    public Director getDirectorById(Integer directorId) {
        return filmStorage.getDirectorById(directorId);
    }

    public Director addDirector(Director director) {
            if (director.getName()!= null) {
                if (!director.getName().isBlank()) {
                    return filmStorage.addDirector(director);
                } else {
                    throw new ValidationException("Invalid Director name");
                }
            } else throw new ValidationException("Invalid director");
    }

    public void deleteDirector(Integer directorId) {
        filmStorage.deleteDirector(directorId);
    }

    public Director updateDirector(Director director) {
        return filmStorage.updateDirector(director);
    }

    public List<Film> getFilmsForDirector(String sortBy, Integer directorId) {
        if (sortBy.equals("year")) {
            return filmStorage.getFilmsForDirectorSortByYear(directorId);
        }
        if (sortBy.equals("likes")) {
            return filmStorage.getFilmsForDirectorSortByLikes(directorId);
        }
        throw new ValidationException("Invalid request param");
    }

    public Collection<Film> searchFilms(String query, List<String> categories) {
        List<Film> films = getAllFilms();
        Set<Film> foundFilms = new LinkedHashSet<>();
        for (Film film : films) {
            if (categories.contains(SEARCH_CATEGORY_TITLE) &&
                    film.getName().toLowerCase().contains(query.toLowerCase())) {
                foundFilms.add(film);
            }
            if (categories.contains(SEARCH_CATEGORY_DIRECTOR) && film.getDirectors() != null) {
                for (Director director : film.getDirectors()) {
                    if (director.getName().toLowerCase().contains(query.toLowerCase())) {
                        foundFilms.add(film);
                    }
                }
            }
        }
        return foundFilms;
    }

    //Roma: new methods for ReviewController
    public Review addReview(Review review) {
        checkIfFilmExists(review.getFilmId());
        userService.checkIfUserExists(review.getUserId());
        return filmStorage.addReview(review);
    }

    public Review editReview(Review review) {
        if(checkIfReviewExists(review.getReviewId())) {
            checkIfFilmExists(review.getFilmId());
            userService.checkIfUserExists(review.getUserId());
            return filmStorage.editReview(review);
        } else {
            throw new ReviewDoesNotExistException("Review does not exist");
        }
    }

    public Review getReviewById(Integer reviewId) {
        return filmStorage.getReviewById(reviewId);
    }

    public List<Review> getReviewsForFilm(Integer filmId, int count) {
        checkIfFilmExists(filmId);
        return filmStorage.getReviewsForFilm(filmId, count);
    }

    public void deleteReviewById(Integer reviewId) {
        if(checkIfReviewExists(reviewId)) {
            filmStorage.deleteReviewById(reviewId);
        } else {
            throw new ReviewDoesNotExistException("Review does not exist");
        }
    }

    public void addLikeToReview(Integer reviewId, Integer userId) {
        if(checkIfReviewExists(reviewId)) {
            userService.checkIfUserExists(userId);
            filmStorage.addLikeToReview(reviewId, userId);
        } else {
            throw new ReviewDoesNotExistException("Review does not exist");
        }
    }

    public void addDislikeToReview(Integer reviewId, Integer userId) {
        if(checkIfReviewExists(reviewId)) {
            userService.checkIfUserExists(userId);
            filmStorage.addDislikeToReview(reviewId, userId);
        } else {
            throw new ReviewDoesNotExistException("Review does not exist");
        }
    }

    public void removeLikeToReview(Integer reviewId, Integer userId) {
        if(checkIfReviewExists(reviewId)) {
            userService.checkIfUserExists(userId);
            filmStorage.removeLikeToReview(reviewId, userId);
        } else {
            throw new ReviewDoesNotExistException("Review does not exist");
        }
    }

    public void removeDislikeToReview(Integer reviewId, Integer userId) {
        if(checkIfReviewExists(reviewId)) {
            userService.checkIfUserExists(userId);
            filmStorage.removeDislikeToReview(reviewId, userId);
        } else {
            throw new ReviewDoesNotExistException("Review does not exist");
        }
    }

    public List<Film> getCommonFilms(int userID, int friendID) {
        userService.checkIfUserExists(userID);
        userService.checkIfUserExists(friendID);
        return filmStorage.getCommonFilms(userID, friendID);
    }
}
