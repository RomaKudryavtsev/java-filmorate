package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> filmsData = new HashMap<>();
    private int filmId = 0;

    @Override
    public List<Film> getAllFilms() {
        return filmsData.values().stream().collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) throws FilmAlreadyExistsException {
        if(filmsData.containsKey(film.getId())) {
            throw new FilmAlreadyExistsException();
        }
        ++filmId;
        film.setId(filmId);
        filmsData.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws FilmToBeUpdatedDoesNotExistException {
        if(!filmsData.containsKey(film.getId())) {
            throw new FilmToBeUpdatedDoesNotExistException();
        }
        filmsData.put(film.getId(), film);
        return film;
    }
}
