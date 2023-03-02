package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingDao ratingDao;
    private final GenreDao genreDao;
    private final GenreFilmDao genreFilmDao;
    private final FilmLikesDao filmLikesDao;
    private final static String SELECT_ALL_INFO_ON_ALL_FILMS_SQL = "select f.film_id," +
            "f.name," +
            "f.description," +
            "f.release_date," +
            "f.duration," +
            "r.rating_id AS rating_id," +
            "r.name AS rating_name " +
            "from film f " +
            "join rating r using(rating_id) ";
    private final static String SELECT_ALL_INFO_ON_FILM_SQL = SELECT_ALL_INFO_ON_ALL_FILMS_SQL + " where f.film_id = ?";
    private final static String SELECT_MOST_LIKED_SQL = "select f.film_id, " +
            "f.name, " +
            "f.description, " +
            "f.release_date, " +
            "f.duration, " +
            "r.rating_id as rating_id, " +
            "r.name as rating_name " +
            "from film f " +
            "inner join rating r using(rating_id) " +
            "left join film_likes fl using(film_id) " +
            "group by f.film_id " +
            "order by count(distinct fl.user_id) DESC " +
            "limit ?";

    private final static String SELECT_MOST_FILMS_BY_YEAR = "select f.FILM_ID as film_id, " +
            "f.NAME as name, " +
            "f.DESCRIPTION as description, " +
            "f.RELEASE_DATE as release_date, " +
            "f.DURATION as duration, " +
            "f.RATING_ID as rating_id, " +
            "r.NAME as rating_name, " +
            "from FILM as f " +
            "left join FILM_LIKES as l on f.FILM_ID = l.FILM_ID " +
            "left join RATING as r on r.RATING_ID = f.RATING_ID " +
            "where extract(year from f.RELEASE_DATE) = ? " +
            "group by f.FILM_ID " +
            "order by COUNT(l.USER_ID) desc " +
            "limit ?";

    private final static String SELECT_MOST_FILMS_BY_GENRE = "select f.FILM_ID as film_id, " +
            "g.GENRE_ID as genre, " +
            "f.NAME as name, " +
            "f.DESCRIPTION as description, " +
            "f.RELEASE_DATE as release_date, " +
            " f.DURATION as duration, " +
            "f.RATING_ID as rating_id, " +
            "r.NAME as rating_name " +
            "from FILM as f " +
            "left join FILM_LIKES as l on f.FILM_ID = l.FILM_ID " +
            "left join GENRE_FILM as g on g.FILM_ID = f.FILM_ID " +
            "left join RATING as r on r.RATING_ID = f.RATING_ID " +
            "where g.GENRE_ID = ? " +
            "group by f.FILM_ID " +
            "order by COUNT(l.USER_ID) desc " +
            "limit ?";

    private final static String SELECT_MOST_FILMS_BY_GENRE_AND_YEAR = " select f.FILM_ID as film_id, " +
            "g.GENRE_ID as genre, " +
            "f.NAME as name, " +
            "f.DESCRIPTION as description, " +
            "f.RELEASE_DATE as release_date, " +
            "f.DURATION as duration, " +
            "f.RATING_ID as rating_id, " +
            "r.NAME as rating_name " +
            "from FILM as f " +
            "left join FILM_LIKES as l on f.FILM_ID = l.FILM_ID " +
            "left join GENRE_FILM as g on g.FILM_ID = f.FILM_ID " +
            "left join RATING as r on r.RATING_ID = f.RATING_ID " +
            "where g.GENRE_ID = ? and extract(year from f.RELEASE_DATE) = ? " +
            "group by f.FILM_ID " +
            "order by COUNT(l.USER_ID) desc " +
            "limit ?";

    private final static String INSERT_NEW_FILM_SQL = "insert into film " +
            "(name, description, release_date, duration, rating_id) " +
            "values(?, ?, ?, ?, ?)";
    private final static String UPDATE_FILM_SQL = "update film set " +
            "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "where film_id = ?";

    private final static String DELETE_FILM_SQL = "delete from film where film_id = ?";

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate, RatingDao ratingDao, GenreDao genreDao, GenreFilmDao genreFilmDao,
                         FilmLikesDao filmLikesDao) {
        this.ratingDao = ratingDao;
        this.genreDao = genreDao;
        this.genreFilmDao = genreFilmDao;
        this.filmLikesDao = filmLikesDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(SELECT_ALL_INFO_ON_ALL_FILMS_SQL, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public List<Film> getMostLikedFilms(int count, int genreId, String year) {
        if ((genreId == 0) && (Objects.equals(year, "null"))) {
            return jdbcTemplate.query(SELECT_MOST_LIKED_SQL, (rs, rowNum) -> makeFilm(rs), count);
        } else if (genreId == 0){
            return jdbcTemplate.query(SELECT_MOST_FILMS_BY_YEAR, (rs, rowNum) -> makeFilm(rs), year, count);
        } else if (Objects.equals(year, "null")){
            return jdbcTemplate.query(SELECT_MOST_FILMS_BY_GENRE, (rs, rowNum) -> makeFilm(rs), genreId, count);
        } else {
            return jdbcTemplate.query(SELECT_MOST_FILMS_BY_GENRE_AND_YEAR,
                    (rs, rowNum) -> makeFilm(rs), genreId, year, count);
        }
    }

    @Override
    public Film addFilm(Film film) throws FilmAlreadyExistsException {
        KeyHolder filmKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_NEW_FILM_SQL, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, handleFilmRating(film.getMpa()));
            return stmt;
        }, filmKeyHolder);
        int filmId = Objects.requireNonNull(filmKeyHolder.getKey()).intValue();
        if(film.getGenres() != null) {
            film.getGenres().stream().
                    forEach(genre -> genreFilmDao.addNewGenreFilm(filmId,
                            handleFilmGenre(genre)));
        }
        //NOTE: DB film_id is assigned to Java object.
        film.setId(filmId);
        log.info("The following film was successfully added: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws FilmToBeUpdatedDoesNotExistException {
        jdbcTemplate.update(UPDATE_FILM_SQL
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , handleFilmRating(film.getMpa())
                , film.getId());
        if(film.getGenres() != null) {
            //NOTE: First, we will add new links corresponding to the given film genre.
            List<Genre> dbGenresOfFilm = genreFilmDao.getGenresForFilm(film.getId());
            film.getGenres().stream()
                    .filter((genre) -> {
                        //NOTE: If link between film and genre already exists - no need to create new row.
                        return !dbGenresOfFilm.stream().map(Genre::getId).collect(Collectors.toSet())
                                .contains(genre.getId());
                    })
                    .forEach((genre) -> {
                        //NOTE: New link between film and genre is created.
                        genreFilmDao.addNewGenreFilm(film.getId(),
                                handleFilmGenre(genre));
                    });
            //NOTE: Second, we have to delete all links in 'genre_film' not corresponding to the given film genres.
            dbGenresOfFilm.stream().filter(genre -> !film.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toSet())
                            .contains(genre.getId()))
                    .forEach((genre) -> genreFilmDao.deleteGenreFilm(film.getId(), genre.getId()));
        }
        film.setMpa(Rating.builder()
                .id(film.getMpa().getId())
                .name(ratingDao.getRatingById(film.getMpa().getId()).getName())
                .build());
        film.setUsersLiked(new HashSet<>(filmLikesDao.getLikesOfFilm(film.getId())));
        log.info("The following film was successfully updated: {}", film);
        return film;
    }

    @Override
    public Film getFilm(int filmId) {
        return jdbcTemplate.query(SELECT_ALL_INFO_ON_FILM_SQL, (rs, rowNum) -> makeFilm(rs), filmId)
                .stream().findFirst().orElseThrow(() -> {throw new FilmDoesNotExistException();});
    }

    //NOTE: Upon deletion of film, we have to delete all links genre-film and film-like.
    @Override
    public void deleteFilm(int filmId) throws FilmDoesNotExistException {
        genreFilmDao.deleteGenreFilmLinksForFilm(filmId);
        filmLikesDao.deleteAllLikesForFilm(filmId);
        jdbcTemplate.update(DELETE_FILM_SQL, filmId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        filmLikesDao.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        filmLikesDao.removeLike(filmId, userId);
    }

    private int handleFilmRating(Rating inputRating) {
        int outputRatingId;
        try {
            //NOTE: If rating is not new, we have to retrieve its id.
            outputRatingId = ratingDao.getRatingById(inputRating.getId()).getId();
        } catch (RatingNotFoundException e) {
            //NOTE: If rating is unknown, it has to be added to table 'rating'.
            outputRatingId = ratingDao.addNewRating(inputRating.getName());
        }
        return outputRatingId;
    }

    private int handleFilmGenre(Genre inputGenre) {
        int outputGenreId;
        try {
            //NOTE: If genre is unknown, it has to be inserted to table 'genre'.
            outputGenreId = genreDao.getGenreById(inputGenre.getId()).getId();
        } catch (GenreNotFoundException e) {
            //NOTE: If genre is not new, we have to retrieve its id.
            outputGenreId = genreDao.addNewGenre(inputGenre.getName());
        }
        return outputGenreId;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int ratingId = rs.getInt("rating_id");
        String ratingName = rs.getString("rating_name");
        Rating rating = Rating.builder()
                .id(ratingId)
                .name(ratingName)
                .build();
        Film result = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(rating)
                .build();
        Set<Integer> usersLiked = new HashSet<>(filmLikesDao.getLikesOfFilm(result.getId()));
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(genreFilmDao.getGenresForFilm(result.getId()));
        result.setUsersLiked(usersLiked);
        result.setGenres(genres);
        return result;
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    @Override
    public Genre getGenreById(int genreId) {
        return genreDao.getGenreById(genreId);
    }

    @Override
    public List<Rating> getAllRatings() {
        return ratingDao.getAllRatings();
    }

    @Override
    public Rating getRatingById(int ratingId) {
        return ratingDao.getRatingById(ratingId);
    }
}
