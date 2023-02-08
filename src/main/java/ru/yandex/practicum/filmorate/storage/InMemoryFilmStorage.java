package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.*;
import java.util.stream.Collectors;

@Component
//@Primary
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> filmsData = new HashMap<>();
    private final Set<Genre> genres = new LinkedHashSet<>();
    private final Set<Rating> ratings = new LinkedHashSet<>();
    private int filmId = 0;

    public InMemoryFilmStorage() {
        initiateGenres();
        initiateRatings();
    }
    private void initiateGenres() {
        genres.add(Genre.builder().id(1).name("Комедия").build());
        genres.add(Genre.builder().id(2).name("Драма").build());
        genres.add(Genre.builder().id(3).name("Мультфильм").build());
        genres.add(Genre.builder().id(4).name("Триллер").build());
        genres.add(Genre.builder().id(5).name("Документальный").build());
        genres.add(Genre.builder().id(6).name("Боевик").build());
    }

    private void initiateRatings() {
        ratings.add(Rating.builder().id(1).name("G").build());
        ratings.add(Rating.builder().id(2).name("PG").build());
        ratings.add(Rating.builder().id(3).name("PG-13").build());
        ratings.add(Rating.builder().id(4).name("R").build());
        ratings.add(Rating.builder().id(5).name("NC-17").build());
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmsData.values());
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        return this.getAllFilms().stream()
                .sorted((f1, f2) -> -1 * (f1.getAmountOfLikes() - f2.getAmountOfLikes())).
                limit(count).collect(Collectors.toList());
    }

    @Override
    public void addLike(int filmId, int userId) {
        this.getFilm(filmId).addLike(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        this.getFilm(filmId).removeLike(userId);
    }

    @Override
    public Film addFilm(Film film) throws FilmAlreadyExistsException {
        ++filmId;
        film.setId(filmId);
        film.setMpa(getFilmRatingWithName(film));
        if(film.getGenres() != null) {
            film.setGenres(new LinkedHashSet<>(getFilmGenresWithNames(film)));
        } else {
            film.setGenres(new LinkedHashSet<>());
        }
        filmsData.put(film.getId(), film);
        log.info("The following film was successfully added: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws FilmToBeUpdatedDoesNotExistException {
        Set<Integer> usersLikesBeforeUpdate = filmsData.get(film.getId()).getUsersLiked();
        film.setUsersLiked(usersLikesBeforeUpdate);
        film.setMpa(getFilmRatingWithName(film));
        if(film.getGenres() != null) {
            film.setGenres(new LinkedHashSet<>(getFilmGenresWithNames(film)));
        } else {
            film.setGenres(new LinkedHashSet<>());
        }
        filmsData.put(film.getId(), film);
        log.info("The following film was successfully updated: {}", film);
        return film;
    }

    @Override
    public Film getFilm(int filmId) throws FilmDoesNotExistException {
        return filmsData.get(filmId);
    }

    @Override
    public void deleteFilm(int filmId) throws FilmDoesNotExistException {
        filmsData.remove(filmId);
    }

    private Rating getFilmRatingWithName(Film film) {
        int filmRatingId = film.getMpa().getId();
        return ratings.stream()
                .filter(r -> r.getId() == filmRatingId)
                .findFirst().orElseThrow(() -> {throw new FilmDoesNotExistException();});
    }

    private Set<Genre> getFilmGenresWithNames(Film film) {
        Set<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId)
                .collect(Collectors.toSet());
        return genres.stream()
                .filter(g -> filmGenreIds.contains(g.getId()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Genre> getAllGenres() {
        return new ArrayList<>(genres);
    }

    @Override
    public Genre getGenreById(int genreId) {
        return genres.stream().filter(g -> g.getId() == genreId)
                .findFirst().orElseThrow(() -> {throw new GenreNotFoundException("Genre not found");});
    }

    @Override
    public List<Rating> getAllRatings() {
        return new ArrayList<>(ratings);
    }

    @Override
    public Rating getRatingById(int ratingId) {
        return ratings.stream().filter(f -> f.getId() == ratingId)
                .findFirst().orElseThrow(() -> {throw new RatingNotFoundException("Rating not found");});
    }
}
