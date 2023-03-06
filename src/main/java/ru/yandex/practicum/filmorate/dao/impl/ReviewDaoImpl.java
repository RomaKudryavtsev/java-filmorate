package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewLikesDao;
import ru.yandex.practicum.filmorate.exceptions.ReviewDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewLikesDao reviewLikesDao;
    private final static String INSERT_NEW_REVIEW_SQL = "insert into review (content, is_positive, user_id, film_id) " +
            "values (?, ?, ?, ?)";
    private final static String UPDATE_REVIEW_SQL = "update review set content = ?, is_positive = ? " +
            "where review_id = ?";
    private final static String SELECT_REVIEW_BY_ID_SQL = "select * from review where review_id = ?";
    private final static String SELECT_REVIEWS_FOR_FILM_WITH_LIMIT_SQL = "select * from review where " +
            "film_id = ? limit ?";
    private final static String SELECT_ALL_REVIEWS_FOR_FILM_SQL = "select * from review where film_id = ?";
    private final static String SELECT_ALL_REVIEWS_FOR_USER_SQL = "select * from review where user_id = ?";
    private final static String SELECT_ALL_REVIEWS_WITH_LIMIT_SQL = "select * from review limit ?";
    private final static String DELETE_REVIEW_BY_ID_SQL = "delete from review where review_id = ?";

    @Autowired
    public ReviewDaoImpl(JdbcTemplate jdbcTemplate, ReviewLikesDao reviewLikesDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewLikesDao = reviewLikesDao;
    }

    @Override
    public Review addReview(Review review) {
        KeyHolder reviewKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_NEW_REVIEW_SQL, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, reviewKeyHolder);
        int reviewId = Objects.requireNonNull(reviewKeyHolder.getKey()).intValue();
        review.setReviewId(reviewId);
        log.info("The following review was successfully added: {}", review);
        return review;
    }

    //NOTE: Postman test require check that userId and filmId of the review are not changed
    @Override
    public Review editReview(Review review) {
        jdbcTemplate.update(UPDATE_REVIEW_SQL
                , review.getContent()
                , review.getIsPositive()
                , review.getReviewId());
        log.info("The following review was successfully edited: {}", review);
        return getReviewById(review.getReviewId());
    }

    //NOTE: Upon deletion of review we have to delete all links review-like
    @Override
    public void deleteReviewById(int reviewId) {
        reviewLikesDao.deleteLikesAndDislikesByReviewId(reviewId);
        jdbcTemplate.update(DELETE_REVIEW_BY_ID_SQL, reviewId);
    }

    @Override
    public void deleteReviewsForFilm(int filmId) {
        jdbcTemplate.query(SELECT_ALL_REVIEWS_FOR_FILM_SQL, (rs, rowNum) -> makeReview(rs), filmId)
                .forEach(r -> deleteReviewById(r.getReviewId()));
    }

    @Override
    public void deleteReviewsForUser(int userId) {
        jdbcTemplate.query(SELECT_ALL_REVIEWS_FOR_USER_SQL, (rs, rowNum) -> makeReview(rs), userId)
                .forEach(r -> deleteReviewById(r.getReviewId()));
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return jdbcTemplate.query(SELECT_ALL_REVIEWS_WITH_LIMIT_SQL, (rs, rowNum) -> makeReview(rs), count)
                .stream().sorted(Comparator.comparing(Review::getUseful).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<Review> getReviewsForFilm(int filmId, int count) {
        return jdbcTemplate.query(SELECT_REVIEWS_FOR_FILM_WITH_LIMIT_SQL, (rs, rowNum) -> makeReview(rs), filmId, count)
                .stream().sorted(Comparator.comparing(Review::getUseful).reversed()).collect(Collectors.toList());
    }

    @Override
    public Review getReviewById(int reviewId) {
        return jdbcTemplate.query(SELECT_REVIEW_BY_ID_SQL, (rs, rowNum) -> makeReview(rs), reviewId)
                .stream().findFirst()
                .orElseThrow(() -> {
                    throw new ReviewDoesNotExistException("Review does not exists");
                });
    }

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        reviewLikesDao.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        reviewLikesDao.addDislike(reviewId, userId);
    }

    @Override
    public void removeLike(Integer reviewId, Integer userId) {
        reviewLikesDao.removeLike(reviewId, userId);
    }

    @Override
    public void removeDislike(Integer reviewId, Integer userId) {
        reviewLikesDao.removeDislike(reviewId, userId);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        int id = rs.getInt("review_id");
        String content = rs.getString("content");
        boolean isPositive = rs.getBoolean("is_positive");
        int userId = rs.getInt("user_id");
        int filmId = rs.getInt("film_id");
        return Review.builder()
                .reviewId(id)
                .content(content)
                .isPositive(isPositive)
                .userId(userId)
                .filmId(filmId)
                .useful(reviewLikesDao.getUsefulRateForReview(id))
                .build();
    }
}
