package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
@Component
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Director> getAllDirectors() {
        return null;
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        return null;
    }

    @Override
    public Director addDirector(Director director) {
        return null;
    }

    @Override
    public void deleteDirector(Integer directorId) {

    }

    @Override
    public Director updateDirector(Director director) {
        return null;
    }
}
