package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorFilmDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DirectorFilmDaoImpl implements DirectorFilmDao {
    private final JdbcTemplate jdbcTemplate;

    private final static String INSERT_NEW_FILM_DIRECTOR_SQL = "INSERT INTO director_film values(?,?)";
    private final static String SELECT_DIRECTORS_FOR_FILM_SQL = "SELECT * FROM director " +
            "JOIN director_film USING(director_id) WHERE film_id = ?";
    private final static String DELETE_DIRECTOR_FOR_FILM_SQL = "DELETE FROM director_film WHERE film_id = ? AND director_id = ?";
    private final static String DELETE_ALL_DIRECTORS_FOR_FILM_SQL = "DELETE FROM director_film WHERE film_id = ?";
    private final static String DELETE_ALL_DIRECTOR_LINKS_SQL = "DELETE FROM director_film WHERE director_id = ?";

    @Autowired
    public DirectorFilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addNewFilmDirector(Integer filmId, Integer directorId) {
        jdbcTemplate.update(INSERT_NEW_FILM_DIRECTOR_SQL, filmId, directorId);
    }

    @Override
    public List<Director> getDirectorsForFilm(Integer filmId) {
        return jdbcTemplate.query(SELECT_DIRECTORS_FOR_FILM_SQL, ((rs, rowNum) -> makeDirector(rs)), filmId);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder().id(rs.getInt("director_id")).name(rs.getString("name")).build();
    }

    @Override
    public void deleteDirectorFilm(Integer filmId, Integer directorId) {
        jdbcTemplate.update(DELETE_DIRECTOR_FOR_FILM_SQL, filmId, directorId);
    }

    @Override
    public void deleteAllDirectorsForFilm(Integer filmId) {
        jdbcTemplate.update(DELETE_ALL_DIRECTORS_FOR_FILM_SQL, filmId);
    }

    @Override
    public void deleteAllDirectorLinks(Integer directorId) {
        jdbcTemplate.update(DELETE_ALL_DIRECTOR_LINKS_SQL, directorId);
    }

}
