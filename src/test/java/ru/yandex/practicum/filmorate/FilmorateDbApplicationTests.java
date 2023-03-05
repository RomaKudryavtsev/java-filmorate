package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateDbApplicationTests {
    private final UserService userService;
    private final FilmService filmService;
    //NOTE: All tests have to be run together.
    @BeforeEach
    void beforeEach() {
        User user1 = userService.addUser(User.builder()
                .login("JohnConnor")
                .email("jconnor@gmail.com")
                .birthday(LocalDate.of(1985, 2, 28))
                .build());
        User user2 = userService.addUser(User.builder()
                .name("T-1000")
                .login("T1000")
                .email("t1000@gmail.com")
                .birthday(LocalDate.of(2023, 1, 1))
                .build());
        User user3 = userService.addUser(User.builder()
                .login("KReese")
                .name("Kyle Reese")
                .email("terminator@mail.com")
                .birthday(LocalDate.of(1980, 8,20))
                .build());
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(Genre.builder().id(1).build());
        Film film1 = filmService.addFilm(Film.builder()
                .name("Terminator")
                .usersLiked(new HashSet<>())
                .description("I'll be back!")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .mpa(Rating.builder().id(1).build())
                .genres(genres)
                .build());
        Film film2 = filmService.addFilm(Film.builder()
                .name("Terminator 3")
                .usersLiked(new HashSet<>())
                .description("Rise of the Machines.")
                .releaseDate(LocalDate.of(2003, 6, 30))
                .duration(109)
                .mpa(Rating.builder().id(1).build())
                .genres(genres)
                .build());
        //NOTE: Add film with unknown genre and rating
        LinkedHashSet<Genre> genres1 = new LinkedHashSet<>();
        genres1.add(Genre.builder().id(7).name("Action").build());
        Film film3 = filmService.addFilm(Film.builder()
                .name("Terminator 2")
                .description("Another great film.")
                .releaseDate(LocalDate.of(1991, 7, 1))
                .duration(137)
                .mpa(Rating.builder().id(6).name("R-18").build())
                .genres(genres1)
                .build());
        //NOTE: Update film with unknown genre and rating
        genres1.add(Genre.builder().id(8).name("Sci-fi").build());
        filmService.updateFilm(Film.builder()
                .id(film3.getId())
                .name("Terminator 2")
                .description("Another great film.")
                .releaseDate(LocalDate.of(1991, 7, 1))
                .duration(137)
                .mpa(Rating.builder().id(7).name("R-21").build())
                .genres(genres1)
                .build());
        //NOTE: The following film will not have likes.
        filmService.addFilm(Film.builder()
                .name("Terminator 5")
                .usersLiked(new HashSet<>())
                .description("Terminator Genisys")
                .releaseDate(LocalDate.of(2015, 7, 2))
                .duration(126)
                .mpa(Rating.builder().id(1).build())
                .genres(genres)
                .build());
        userService.addToFriends(user1.getId(), user2.getId());
        userService.addToFriends(user2.getId(), user3.getId());
        userService.addToFriends(user3.getId(), user2.getId());
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user3.getId());
    }

    @AfterEach
    void afterEach() {
        filmService.getAllFilms().stream().map(Film::getId)
                .forEach(filmService::deleteFilmById);
        userService.getAllUsers().stream().map(User::getId)
                .forEach(userService::deleteUserById);
    }

    //NOTE: In tests auto increment for ids is not restarted.
    private int getFilmId(String filmName) {
        return filmService.getAllFilms().stream()
                .filter(f -> f.getName().equals(filmName))
                .findFirst().get().getId();
    }

    private int getUserId(String userName) {
        return userService.getAllUsers().stream()
                .filter(u -> u.getName().equals(userName))
                .findFirst().get().getId();
    }

    @Test
    void testAddLike(){
        Assertions.assertEquals(2, filmService.getFilmById(getFilmId("Terminator"))
                .getAmountOfLikes());
        Assertions.assertEquals(1, filmService.getFilmById(getFilmId("Terminator 3"))
                .getAmountOfLikes());
    }

    @Test
    void testCancelLike() {
        filmService.cancelLike(getFilmId("Terminator"), getUserId("JohnConnor"));
        Assertions.assertEquals(1, filmService.getFilmById(getFilmId("Terminator"))
                .getAmountOfLikes());
    }

    @Test
    void testGetMostLikedFilms() {
        Assertions.assertEquals(4, filmService.getMostLikedFilms(10, -1, -1).size());
    }

    @Test
    void testGetAllFilms() {
        Assertions.assertEquals(4, filmService.getAllFilms().size());
    }

    @Test
    void testAddFilm() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(Genre.builder().id(1).build());
        filmService.addFilm(Film.builder()
                .name("Terminator 2")
                .description("Another great film.")
                .releaseDate(LocalDate.of(1991, 7, 1))
                .duration(137)
                .mpa(Rating.builder().id(1).build())
                .genres(genres)
                .build());
        Assertions.assertEquals(5, filmService.getAllFilms().size());
    }

    @Test
    void testGetFilmById() {
        Assertions.assertEquals(getFilmId("Terminator 3"),
                filmService.getFilmById(getFilmId("Terminator 3")).getId());
    }

    @Test
    void testUpdateFilm() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(Genre.builder().id(1).build());
        Film film = filmService.updateFilm(Film.builder()
                .id(getFilmId("Terminator"))
                .name("Terminator")
                .usersLiked(new HashSet<>())
                .description("I'll be back!")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .mpa(Rating.builder().id(2).build())
                .genres(genres)
                .build());
        Assertions.assertEquals(2, filmService.getFilmById(film.getId()).getMpa().getId());
    }

    @Test
    void testUpdateFilmWithUnknownRatingAndGetAllRatings() {
        Assertions.assertEquals(7, filmService.getAllRatings().size());
        Assertions.assertEquals("R-21", filmService.getRatingById(7).getName());
        Assertions.assertEquals("R-21",
                filmService.getFilmById(getFilmId("Terminator 2")).getMpa().getName());

    }

    @Test
    void testUpdateFilmWithUnknownGenreAndGetAllGenres() {
        Assertions.assertEquals(8, filmService.getAllGenres().size());
        Assertions.assertEquals("Sci-fi", filmService.getGenreById(8).getName());
        Assertions.assertEquals(2,
                filmService.getFilmById(getFilmId("Terminator 2")).getGenres().size());
    }

    @Test
    void testGetGenreById() {
        Assertions.assertEquals("Комедия", filmService.getGenreById(1).getName());
    }

    @Test
    void testGetRatingById() {
        Assertions.assertEquals("G", filmService.getRatingById(1).getName());
    }

    @Test
    void testAddToFriends() {
        Assertions.assertEquals(1, userService.getListOfUsersFriends(getUserId("JohnConnor")).size());
        Assertions.assertEquals(1, userService.getListOfUsersFriends(getUserId("Kyle Reese")).size());
    }

    @Test
    void testDeleteFromFriends() {
        userService.deleteFromFriends(getUserId("JohnConnor"), getUserId("T-1000"));
        Assertions.assertEquals(0, userService.getListOfUsersFriends(getUserId("JohnConnor")).size());
    }

    @Test
    void testGetListOfUsersFriends() {
        Assertions.assertEquals(1, userService.getListOfUsersFriends(getUserId("JohnConnor")).size());
    }

    @Test
    void testGetListOfCommonFriends() {
        Assertions.assertEquals(1, userService.getListOfCommonFriends(getUserId("JohnConnor"),
                getUserId("Kyle Reese")).size());
    }

    @Test
    void testGetAllUsers() {
        Assertions.assertEquals(3, userService.getAllUsers().size());
    }

    @Test
    void testAddUser() {
        userService.addUser(User.builder()
                .name("Terminator")
                .login("Terminator")
                .email("arnie@gmail.com")
                .birthday(LocalDate.of(1947, 7, 30))
                .build());
        Assertions.assertEquals(4, userService.getAllUsers().size());
    }

    @Test
    void testGetUserById() {
        Assertions.assertEquals(getUserId("JohnConnor"),
                userService.getUserById(getUserId("JohnConnor")).getId());
    }

    @Test
    void testUpdateUser() {
        User user = userService.updateUser(User.builder()
                .id(getUserId("T-1000"))
                .name("T-1000")
                .login("T-1000")
                .email("t1000@gmail.com")
                .birthday(LocalDate.of(2023, 1, 1))
                .build());
        Assertions.assertEquals(user.getLogin(), userService.getUserById(getUserId("T-1000")).getLogin());
    }
}
