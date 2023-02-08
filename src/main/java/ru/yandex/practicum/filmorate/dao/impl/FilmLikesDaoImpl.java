package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikesDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmLikesDaoImpl implements FilmLikesDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String SELECT_FILM_LIKES_SQL = "select user_id from film_likes where film_id = ?";
    private final static String INSERT_NEW_FILM_LIKE_SQL = "insert into film_likes values(?, ?)";
    private final static String DELETE_FILM_LIKE_SQL = "delete from film_likes where film_id = ? and user_id = ?";
    private final static String DELETE_ALL_LIKES_FOR_FILM_SQL = "delete from film_likes where film_id = ?";
    private final static String DELETE_ALL_LIKES_OF_USER_SQL = "delete from film_likes where user_id = ?";

    @Autowired
    public FilmLikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(INSERT_NEW_FILM_LIKE_SQL, filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(DELETE_FILM_LIKE_SQL, filmId, userId);
    }

    @Override
    public Set<Integer> getLikesOfFilm(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query(SELECT_FILM_LIKES_SQL, (rs, rowNum) -> makeUserId(rs), filmId));
    }

    @Override
    public void deleteAllLikesForFilm(Integer filmId) {
        jdbcTemplate.update(DELETE_ALL_LIKES_FOR_FILM_SQL, filmId);
    }

    @Override
    public void deleteAllLikesOfUser(Integer userId) {
        jdbcTemplate.update(DELETE_ALL_LIKES_OF_USER_SQL, userId);
    }

    private Integer makeUserId(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }
}
