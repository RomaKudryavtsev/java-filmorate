package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewLikesDao;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewLikesDaoImpl implements ReviewLikesDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String INSERT_NEW_REVIEW_LIKE_SQL = "insert into review_likes values(?, ?, true)";
    private final static String INSERT_NEW_REVIEW_DISLIKE_SQL = "insert into review_likes values(?, ?, false)";
    private final static String DELETE_REVIEW_LIKE_OR_DISLIKE_SQL = "delete from review_likes where review_id = ? " +
            "and user_id = ?";
    private final static String DELETE_ALL_REVIEW_LIKES_OR_DISLIKES_BY_REVIEW_ID_SQL = "delete from review_likes " +
            "where review_id = ?";
    private final static String SELECT_DISLIKES_AMOUNT_FOR_REVIEW_SQL = "select count(*) AS count from review_likes " +
            "where review_id = ? AND is_like = false";
    private final static String SELECT_LIKES_AMOUNT_FOR_REVIEW_SQL = "select count(*) AS count from review_likes " +
            "where review_id = ? AND is_like = true";

    @Autowired
    public ReviewLikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike (Integer reviewId, Integer userId) {
        jdbcTemplate.update(INSERT_NEW_REVIEW_LIKE_SQL, reviewId, userId);
    }

    @Override
    public void addDislike (Integer reviewId, Integer userId) {
        jdbcTemplate.update(INSERT_NEW_REVIEW_DISLIKE_SQL, reviewId, userId);
    }

    @Override
    public void removeLike (Integer reviewId, Integer userId) {
        jdbcTemplate.update(DELETE_REVIEW_LIKE_OR_DISLIKE_SQL, reviewId, userId);
    }

    @Override
    public void removeDislike (Integer reviewId, Integer userId) {
        jdbcTemplate.update(DELETE_REVIEW_LIKE_OR_DISLIKE_SQL, reviewId, userId);
    }

    @Override
    public int getUsefulRateForReview(int reviewId) {
        int likesCounter = jdbcTemplate
                .query(SELECT_LIKES_AMOUNT_FOR_REVIEW_SQL, (rs, rowNum) -> makeCount(rs), reviewId)
                .stream().findFirst().get();
        int dislikeCounter = jdbcTemplate
                .query(SELECT_DISLIKES_AMOUNT_FOR_REVIEW_SQL, (rs, rowNum) -> makeCount(rs), reviewId)
                .stream().findFirst().get();
        return likesCounter - dislikeCounter;
    }

    @Override
    public void deleteLikesAndDislikesByReviewId(Integer reviewId) {
        jdbcTemplate.update(DELETE_ALL_REVIEW_LIKES_OR_DISLIKES_BY_REVIEW_ID_SQL, reviewId);
    }

    private Integer makeCount(ResultSet rs) throws SQLException {
        return rs.getInt("count");
    }
}
