package ru.yandex.practicum.filmorate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@Api(tags = "Reviews")
public class ReviewController {
    private final FilmService filmService;
    private final static String GENERAL_REVIEW_PATH = "/reviews";
    private final static String ALTER_LIKE_PATH = GENERAL_REVIEW_PATH + "/{id}/like/{userId}";
    private final static String ALTER_DISLIKE_PATH = GENERAL_REVIEW_PATH + "/{id}/dislike/{userId}";

    @Autowired
    public ReviewController(FilmService filmService) {
        this.filmService = filmService;
    }

    @ApiOperation(value = "Add review")
    @PostMapping(value = GENERAL_REVIEW_PATH)
    public Review addReview(@Valid @RequestBody Review review) {
        return filmService.addReview(review);
    }

    @ApiOperation(value = "Edit review")
    @PutMapping(value = GENERAL_REVIEW_PATH)
    public Review editReview(@Valid @RequestBody Review review) {
        return filmService.editReview(review);
    }

    @ApiOperation(value = "Get review by id")
    @GetMapping(GENERAL_REVIEW_PATH + "/{id}")
    public Review getReviewById(@PathVariable("id") Integer reviewId) {
        return filmService.getReviewById(reviewId);
    }

    @ApiOperation(value = "Get reviews for certain film")
    @GetMapping(GENERAL_REVIEW_PATH)
    public List<Review> getReviewsForFilm(@RequestParam(required = false) Integer filmId,
                                          @RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getReviewsForFilm(filmId, count);
    }

    @ApiOperation(value = "Add like to review")
    @PutMapping(value = ALTER_LIKE_PATH)
    public void addLike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        filmService.addLikeToReview(reviewId, userId);
    }

    @ApiOperation(value = "Add dislike to review")
    @PutMapping(value = ALTER_DISLIKE_PATH)
    public void addDislike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        filmService.addDislikeToReview(reviewId, userId);
    }

    @ApiOperation(value = "Remove like to review")
    @DeleteMapping(value = ALTER_LIKE_PATH)
    public void removeLike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        filmService.removeLikeToReview(reviewId, userId);
    }

    @ApiOperation(value = "Remove dislike to review")
    @DeleteMapping(value = ALTER_DISLIKE_PATH)
    public void removeDislike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        filmService.removeDislikeToReview(reviewId, userId);
    }

    @ApiOperation(value = "Delete review")
    @DeleteMapping(value = GENERAL_REVIEW_PATH + "/{id}")
    public void deleteReview(@PathVariable("id") Integer reviewId) {
        filmService.deleteReviewById(reviewId);
    }
}
