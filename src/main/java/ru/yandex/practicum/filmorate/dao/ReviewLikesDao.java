package ru.yandex.practicum.filmorate.dao;

public interface ReviewLikesDao {
    void addLike(Integer reviewId, Integer userId);

    void addDislike(Integer reviewId, Integer userId);

    void removeLike(Integer reviewId, Integer userId);

    void removeDislike(Integer reviewId, Integer userId);

    int getUsefulRateForReview(int reviewId);

    void deleteLikesAndDislikesByReviewId(Integer reviewId);
}
