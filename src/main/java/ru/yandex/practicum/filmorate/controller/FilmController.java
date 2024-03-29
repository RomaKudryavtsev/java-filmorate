package ru.yandex.practicum.filmorate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@Api(tags = "Films")
public class FilmController {
    private final FilmService filmService;
    private final static String GENERAL_FILMS_PATH = "/films";
    private final static String ALTER_LIKES_PATH = GENERAL_FILMS_PATH + "/{id}/like/{userId}";
    private final static String SEARCH_PATH = GENERAL_FILMS_PATH + "/search";
    private final static String GENERAL_GENRES_PATH = "/genres";
    private final static String GENERAL_RATINGS_PATH = "/mpa";
    private final static String GENERAL_DIRECTORS_PATH = "/directors";


    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @ApiOperation(value = "Add like to film")
    @PutMapping(value = ALTER_LIKES_PATH)
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @ApiOperation(value = "Delete like to film")
    @DeleteMapping(value = ALTER_LIKES_PATH)
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.cancelLike(filmId, userId);
    }

    @ApiOperation(value = "Find most popular films")
    @GetMapping(GENERAL_FILMS_PATH + "/popular")
    public List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                           @RequestParam(defaultValue = "-1", required = false) Integer genreId,
                                           @RequestParam(defaultValue = "-1", required = false) Integer year) {
        return filmService.getMostLikedFilms(count, genreId, year);
    }

    @ApiOperation(value = "Find film by id")
    @GetMapping(GENERAL_FILMS_PATH + "/{id}")
    public Film getFilmById(@PathVariable("id") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @ApiOperation(value = "Get all films")
    @GetMapping(GENERAL_FILMS_PATH)
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @ApiOperation(value = "Add film")
    @PostMapping(value = GENERAL_FILMS_PATH)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @ApiOperation(value = "Update film")
    @PutMapping(value = GENERAL_FILMS_PATH)
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @ApiOperation(value = "Get all genres")
    @GetMapping(GENERAL_GENRES_PATH)
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @ApiOperation(value = "Get genre by id")
    @GetMapping(GENERAL_GENRES_PATH + "/{id}")
    public Genre getGenreById(@PathVariable("id") Integer genreId) {
        return filmService.getGenreById(genreId);
    }

    @ApiOperation(value = "Get all ratings")
    @GetMapping(GENERAL_RATINGS_PATH)
    public List<Rating> getAllRatings() {
        return filmService.getAllRatings();
    }

    @ApiOperation(value = "Get rating by id")
    @GetMapping(GENERAL_RATINGS_PATH + "/{id}")
    public Rating getRatingById(@PathVariable("id") Integer ratingId) {
        return filmService.getRatingById(ratingId);
    }

    @ApiOperation(value = "Search films")
    @GetMapping(SEARCH_PATH)
    public Collection<Film> searchFilms(@RequestParam Optional<String> query, @RequestParam("by") Optional<List<String>> categories) {
        if (query.isPresent() && categories.isPresent()) {
            return filmService.searchFilms(query.get(), categories.get());
        } else {
            return filmService.getMostLikedFilms(-1, -1, -1);
        }
    }

    @ApiOperation(value = "Get all directors")
    @GetMapping(GENERAL_DIRECTORS_PATH)
    public List<Director> getAllDirectors() {
        return filmService.getAllDirectors();
    }

    @ApiOperation(value = "Get director by id")
    @GetMapping(GENERAL_DIRECTORS_PATH + "/{id}")
    public Director getDirectorById(@PathVariable("id") Integer directorId) {
        return filmService.getDirectorById(directorId);
    }

    @ApiOperation(value = "Add director")
    @PostMapping(GENERAL_DIRECTORS_PATH)
    public Director addDirector(@RequestBody Director director) {
        return filmService.addDirector(director);
    }

    @ApiOperation(value = "Delete director")
    @DeleteMapping(GENERAL_DIRECTORS_PATH + "/{id}")
    public void deleteDirector(@PathVariable("id") Integer directorId) {
        filmService.deleteDirector(directorId);
    }

    @ApiOperation(value = "Update director")
    @PutMapping(GENERAL_DIRECTORS_PATH)
    public Director updateDirector(@RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @ApiOperation(value = "Get films of certain director")
    @GetMapping("/films/director/{directorId}")
    public List<Film> getFilmsForDirector(@RequestParam(name = "sortBy") String sortBy, @PathVariable Integer directorId) {
        return filmService.getFilmsForDirector(sortBy, directorId);
    }

    @ApiOperation(value = "Delete film")
    @DeleteMapping(value = GENERAL_FILMS_PATH + "/{id}")
    public void deleteFilmById(@PathVariable("id") Integer filmId) {
        filmService.deleteFilmById(filmId);
    }

    @ApiOperation(value = "Get common films")
    @GetMapping(GENERAL_FILMS_PATH + "/common")
    public List<Film> getCommonFilms(
            @RequestParam int userId,
            @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
