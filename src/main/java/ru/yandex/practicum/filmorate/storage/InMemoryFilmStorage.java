package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> filmsData = new HashMap<>();
    private int filmId = 0;

    private boolean validateFilm(Film film) throws ValidationException {
        if(film.getDescription().length() > 200) {
            throw new ValidationException("Film description may not exceed 200 symbols");
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Invalid film release date");
        }
        if(film.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive number");
        }
        return true;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmsData.values());
    }

    @Override
    public Film addFilm(Film film) throws FilmAlreadyExistsException {
        if(filmsData.containsKey(film.getId())) {
            throw new FilmAlreadyExistsException("This film already exists");
        }
        if(film.getUsersLiked() == null) {
            film.setUsersLiked(new HashSet<>());
        }
        validateFilm(film);
        ++filmId;
        film.setId(filmId);
        filmsData.put(film.getId(), film);
        log.info("The following film was successfully added: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws FilmToBeUpdatedDoesNotExistException {
        if(!filmsData.containsKey(film.getId())) {
            throw new FilmToBeUpdatedDoesNotExistException("Film to be updated does not exist");
        }
        Set<Integer> usersLikesBeforeUpdate = filmsData.get(film.getId()).getUsersLiked();
        film.setUsersLiked(usersLikesBeforeUpdate);
        filmsData.put(film.getId(), film);
        log.info("The following film was successfully updated: {}", film);
        return film;
    }

    @Override
    public Film getFilm(int filmId) throws FilmDoesNotExistException {
        if(!filmsData.containsKey(filmId)) {
            throw new FilmDoesNotExistException("This film does not exist");
        }
        return filmsData.get(filmId);
    }
}
