package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;
    private final static String SELECT_ALL_DIRECTORS_SQL = "SELECT * FROM director";
    private final static String SELECT_DIRECTOR_BY_ID_SQL = "SELECT * FROM DIRECTOR WHERE director_id = ?";
    private final static String INSERT_DIRECTOR_SQL = "INSERT INTO director (name) values(?)";
    private final static String DELETE_DIRECTOR_SQL = "DELETE FROM director WHERE director_id = ?";
    private final static String UPDATE_DIRECTOR_SQL = "UPDATE director SET " +
            "name = ? " +
            "WHERE director_id = ?";

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(SELECT_ALL_DIRECTORS_SQL, (rs, rowNum) -> makeDirector(rs));
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public Director getDirectorById(Integer directorId) {

        return jdbcTemplate.query(SELECT_DIRECTOR_BY_ID_SQL, (rs, rowNum) -> makeDirector(rs), directorId)
                .stream().findFirst().orElseThrow(() -> {
                    throw new DirectorNotFoundException("Нет такого режиссера");
                });
    }

    @Override
    public Director addDirector(Director director) {
        jdbcTemplate.update(INSERT_DIRECTOR_SQL, director.getName());
        return jdbcTemplate.query(SELECT_ALL_DIRECTORS_SQL + " ORDER BY director_id DESC LIMIT 1", (rs, rowNum) -> makeDirector(rs))
                .stream().findFirst().orElseThrow(() -> {
                    throw new DirectorNotFoundException();
                });
    }

    @Override
    public void deleteDirector(Integer directorId) {
        getDirectorById(directorId);
        jdbcTemplate.update(DELETE_DIRECTOR_SQL, directorId);
    }

    @Override
    public Director updateDirector(Director director) {
        getDirectorById(director.getId());
        jdbcTemplate.update(UPDATE_DIRECTOR_SQL, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }
}
