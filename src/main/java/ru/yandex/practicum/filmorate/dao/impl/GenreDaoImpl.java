package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String SELECT_ALL_GENRES_SQL = "select * from genre";
    private final static String SELECT_GENRE_BY_ID_SQL = "select * from genre where genre_id = ?";
    private final static String SELECT_ALL_GENRE_NAMES_SQL = "select name from genre";
    private final static String SELECT_GENRE_ID_FOR_GENRE_NAME_SQL = "select genre_id from genre where name = ?";
    private final static String INSERT_NEW_GENRE_SQL = "insert into genre (name) values(?)";

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SELECT_ALL_GENRES_SQL, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Integer addNewGenre(String name) {
        KeyHolder genreKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_NEW_GENRE_SQL, new String[]{"genre_id"});
            stmt.setString(1, name);
            return stmt;
        }, genreKeyHolder);
        return Objects.requireNonNull(genreKeyHolder.getKey()).intValue();
    }

    @Override
    public List<String> getAllGenreNames() {
        return jdbcTemplate.query(SELECT_ALL_GENRE_NAMES_SQL, (rs, rowNum) -> makeGenreName(rs));
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        return jdbcTemplate.query(SELECT_GENRE_BY_ID_SQL, (rs, rowNum) -> makeGenre(rs), genreId)
                .stream().findFirst().orElseThrow(()-> {throw new GenreNotFoundException("Genre not found");});
    }

    @Override
    public Integer getGenreIdForGenreName(String name) {
        return jdbcTemplate.query(SELECT_GENRE_ID_FOR_GENRE_NAME_SQL, (rs, rowNum) -> makeGenreId(rs), name)
                .stream().findFirst().orElseThrow(() -> {throw new GenreNotFoundException();});
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }

    private String makeGenreName(ResultSet rs) throws SQLException {
        return rs.getString("name");
    }

    private Integer makeGenreId(ResultSet rs) throws SQLException {
        return rs.getInt("genre_id");
    }
}
