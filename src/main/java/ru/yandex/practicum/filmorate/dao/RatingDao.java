package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingDao {
    List<Rating> getAllRatings();
    Integer getRatingIdForRatingName(String name);
    Integer addNewRating(String name);
    Rating getRatingById(Integer ratingId);
}
