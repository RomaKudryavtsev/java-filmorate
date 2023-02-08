package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreFilmDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreFilmDaoImpl implements GenreFilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String SELECT_GENRES_FOR_FILM_SQL = "select * from genre " +
            "join genre_film using(genre_id) where film_id = ?";
    private final static String INSERT_NEW_GENRE_FILM_SQL = "insert into genre_film values(?, ?)";
    private final static String DELETE_GENRE_FILM_SQL = "delete from genre_film where film_id = ? and genre_id = ?";
    private final static String DELETE_GENRE_FILM_LINKS_FOR_FILM_SQL = "delete from genre_film where film_id = ?";

    @Autowired
    public GenreFilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addNewGenreFilm(Integer filmId, Integer genreId) {
        jdbcTemplate.update(INSERT_NEW_GENRE_FILM_SQL, filmId, genreId);
    }

    @Override
    public List<Genre> getGenresForFilm(Integer filmId) {
        return jdbcTemplate.query(SELECT_GENRES_FOR_FILM_SQL, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    @Override
    public void deleteGenreFilm(Integer filmId, Integer genreId){
        jdbcTemplate.update(DELETE_GENRE_FILM_SQL, filmId, genreId);
    }

    @Override
    public void deleteGenreFilmLinksForFilm(Integer filmId) {
        jdbcTemplate.update(DELETE_GENRE_FILM_LINKS_FOR_FILM_SQL, filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
