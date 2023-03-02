package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {
    Review addReview (Review review);

    Review editReview (Review review);

    void deleteReviewById (int reviewId);

    void deleteReviewsForFilm(int filmId);

    void deleteReviewsForUser(int userId);

    List<Review> getAllReviews (int count);

    void addLike (Integer reviewId, Integer userId);

    void addDislike (Integer reviewId, Integer userId);

    void removeLike (Integer reviewId, Integer userId);

    void removeDislike (Integer reviewId, Integer userId);

    Review getReviewById (int reviewId);

    List<Review> getReviewsForFilm (int filmId, int count);
}
