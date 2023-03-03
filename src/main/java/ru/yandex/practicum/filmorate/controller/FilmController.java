package ru.yandex.practicum.filmorate.controller;

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
public class FilmController {
    //NOTE: FilmController is dependent from FilmService
    private final FilmService filmService;
    private final static String ALTER_LIKES_PATH = "/films/{id}/like/{userId}";
    private final static String GENERAL_FILMS_PATH = "/films";
    private final static String GENERAL_SEARCH_PATH = "/films/search";
    private final static String GENERAL_GENRES_PATH = "/genres";
    private final static String GENERAL_RATINGS_PATH = "/mpa";
    private final static String GENERAL_DIRECTORS_PATH = "/directors";
    private static final String DEFAULT_TOP_FILMS_COUNT = "10";


    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PutMapping(value = ALTER_LIKES_PATH)
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(value = ALTER_LIKES_PATH)
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.cancelLike(filmId, userId);
    }

    @GetMapping(GENERAL_FILMS_PATH + "/popular")
    public List<Film> findMostPopularFilms(@RequestParam(defaultValue = DEFAULT_TOP_FILMS_COUNT, required = false) Integer count) {
        return filmService.getMostLikedFilms(count);
    }

    @GetMapping(GENERAL_FILMS_PATH + "/{id}")
    public Film getFilmById(@PathVariable("id") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping (GENERAL_FILMS_PATH)
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping(value = GENERAL_FILMS_PATH)
    public Film addFilm (@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping(value = GENERAL_FILMS_PATH)
    public Film updateFilm (@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping (GENERAL_GENRES_PATH)
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping(GENERAL_GENRES_PATH + "/{id}")
    public Genre getGenreById(@PathVariable("id") Integer genreId) {
        return filmService.getGenreById(genreId);
    }

    @GetMapping(GENERAL_RATINGS_PATH)
    public List<Rating> getAllRatings() {
        return filmService.getAllRatings();
    }

    @GetMapping(GENERAL_RATINGS_PATH + "/{id}")
    public Rating getRatingById(@PathVariable("id") Integer ratingId) {
        return filmService.getRatingById(ratingId);
    }

    @GetMapping(GENERAL_SEARCH_PATH)
    public Collection<Film> searchFilms(@RequestParam Optional<String> query, @RequestParam("by") Optional<List<String>> categories) {
        if (query.isPresent() && categories.isPresent()) {
            return filmService.searchFilms(query.get(), categories.get());
        } else {
            return filmService.getMostLikedFilms(Integer.parseInt(DEFAULT_TOP_FILMS_COUNT));
        }
    }

    @GetMapping(GENERAL_DIRECTORS_PATH)
    public List<Director> getAllDirectors() {
        return filmService.getAllDirectors();
    }

    @GetMapping(GENERAL_DIRECTORS_PATH + "/{id}")
    public Director getDirectorById(@PathVariable("id") Integer directorId) {
        return filmService.getDirectorById(directorId);
    }

    @PostMapping(GENERAL_DIRECTORS_PATH)
    public Director addDirector (@RequestBody Director director) {
        return filmService.addDirector(director);
    }

    @DeleteMapping(GENERAL_DIRECTORS_PATH + "/{id}")
    public void deleteDirector(@PathVariable("id") Integer directorId) {
        filmService.deleteDirector(directorId);
    }

    @PutMapping(GENERAL_DIRECTORS_PATH)
    public Director updateDirector(@RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @GetMapping("/films/director/{directorId}")
    @ResponseBody
    public List<Film> getFilmsForDirector (@RequestParam(name = "sortBy") String sortBy, @PathVariable Integer directorId) {
        return filmService.getFilmsForDirector(sortBy,directorId);
    }

    @DeleteMapping(value = GENERAL_FILMS_PATH + "/{id}")
    public void deleteFilmById(@PathVariable("id") Integer filmId) {
        filmService.deleteFilmById(filmId);
    }

    @GetMapping(GENERAL_FILMS_PATH + "/common")
    public List<Film> getCommonFilms(
            @RequestParam int userId,
            @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
