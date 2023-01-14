package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class FilmController {
    //NOTE: FilmController is dependent from FilmService
    private final FilmService filmService;
    private final static String ALTER_LIKES_PATH = "/films/{id}/like/{userId}";
    private final static String GENERAL_FILMS_PATH = "/films";

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
    public List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
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

}
