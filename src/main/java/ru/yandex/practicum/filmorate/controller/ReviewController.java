package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class ReviewController {
    private final FilmService filmService;
    private final static String GENERAL_REVIEW_PATH = "/reviews";
    private final static String ALTER_LIKE_PATH = GENERAL_REVIEW_PATH + "/{id}/like/{userId}";
    private final static String ALTER_DISLIKE_PATH = GENERAL_REVIEW_PATH + "/{id}/dislike/{userId}";

    @Autowired
    public ReviewController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping (value = GENERAL_REVIEW_PATH)
    public Review addReview (@Valid @RequestBody Review review) {
        return filmService.addReview(review);
    }

    @PutMapping(value = GENERAL_REVIEW_PATH)
    public Review editReview (@Valid @RequestBody Review review) {
        return filmService.editReview(review);
    }

    @GetMapping(GENERAL_REVIEW_PATH + "/{id}")
    public Review getReviewById(@PathVariable("id") Integer reviewId) {
        return filmService.getReviewById(reviewId);
    }

    @GetMapping(GENERAL_REVIEW_PATH)
    public List<Review> getReviewsForFilm(@RequestParam(required = false) Integer filmId,
                                          @RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getReviewsForFilm(filmId, count);
    }

    @PutMapping (value = ALTER_LIKE_PATH)
    public void addLike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        filmService.addLikeToReview(reviewId, userId);
    }


    @PutMapping (value = ALTER_DISLIKE_PATH)
    public void addDislike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        filmService.addDislikeToReview(reviewId, userId);
    }

    @DeleteMapping (value = ALTER_LIKE_PATH)
    public void removeLike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        filmService.removeLikeToReview(reviewId, userId);
    }

    @DeleteMapping (value = ALTER_DISLIKE_PATH)
    public void removeDislike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        filmService.removeDislikeToReview(reviewId, userId);
    }

    @DeleteMapping (value = GENERAL_REVIEW_PATH + "/{id}")
    public void deleteReview(@PathVariable("id") Integer reviewId) {
        filmService.deleteReviewById(reviewId);
    }
}
