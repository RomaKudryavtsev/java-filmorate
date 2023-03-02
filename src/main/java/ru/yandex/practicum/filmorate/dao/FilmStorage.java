package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film addFilm(Film film) throws FilmAlreadyExistsException;

    Film updateFilm(Film film) throws FilmToBeUpdatedDoesNotExistException;

    Film getFilm(int filmId);

    void deleteFilm(int filmId) throws FilmDoesNotExistException;

    List<Film> getMostLikedFilms(int count, int genreId, String year);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Review addReview(Review review);

    Review editReview(Review review);

    void deleteReviewById(Integer reviewId);

    Review getReviewById(Integer reviewId);

    void addLikeToReview(Integer reviewId, Integer userId);

    void addDislikeToReview(Integer reviewId, Integer userId);

    void removeLikeToReview(Integer reviewId, Integer userId);

    void removeDislikeToReview(Integer reviewId, Integer userId);

    List<Review> getReviewsForFilm(Integer filmId, Integer count);

    List<Genre> getAllGenres();

    Genre getGenreById(int genreId);

    List<Rating> getAllRatings();

    Rating getRatingById(int ratingId);

    List<Film> getCommonFilms(int userID, int friendID);
}
