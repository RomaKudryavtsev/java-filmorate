package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class FilmController {
    private final FilmStorage storage;

    public FilmController() {
        storage = new InMemoryFilmStorage();
    }

    public FilmController(FilmStorage storage) {
        this.storage = storage;
    }

    @GetMapping ("/films")
    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    @PostMapping(value = "/films")
    public Film addFilm (@Valid @RequestBody Film film) {
        try {
            if(film.getDescription().length() > 200) {
                throw new ValidationException("Film description may not exceed 200 symbols");
            }
            if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Invalid film release date");
            }
            if(film.getDuration() <= 0) {
                throw new ValidationException("Duration must be positive number");
            }
            Film addedFilm = storage.addFilm(film);
            log.info("The following film was successfully added: {}", film);
            return addedFilm;
        } catch (FilmAlreadyExistsException e) {
            log.debug("The film already exists");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The film already exists");
        } catch (ValidationException e) {
            log.debug(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping(value = "/films")
    public Film updateFilm (@Valid @RequestBody Film film) {
        try {
            storage.updateFilm(film);
            log.info("The following film was successfully updated: {}", film);
            return film;
        } catch (FilmToBeUpdatedDoesNotExistException e) {
            log.debug("The film to be updated does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The film to be updated does not exist");
        }
    }

}
