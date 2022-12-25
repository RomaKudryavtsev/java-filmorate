package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//NOTE: All tests have to be run together
@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static FilmStorage filmStorage;
    private static UserStorage userStorage;
    private static FilmController filmController;
    private static UserController userController;

    @BeforeAll
    public static void beforeAll() {
        filmStorage = new InMemoryFilmStorage();
        filmController = new FilmController(filmStorage);
        userStorage = new InMemoryUserStorage();
        userController = new UserController(userStorage);
    }

    @Test
    public void testFilmControllerSuccess() {
        Film newFilm = Film.builder()
                .name("Terminator")
                .description("I'll be back!")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .build();
        Film result = filmController.addFilm(newFilm);
        assertEquals(result.getName(), "Terminator");
        assertEquals(filmStorage.getAllFilms().size(), 1);
    }

    @Test
    public void testFailEmptyName() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> Film.builder()
                        .description("Another great film.")
                        .releaseDate(LocalDate.of(1991, 7, 1))
                        .duration(137)
                        .build());
        assertEquals(exception.getMessage(), "name is marked non-null but is null");
    }

    @Test
    public void testFailInvalidDescription() {
        final ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> {
                    Film newFilm = Film.builder()
                            .name("Terminator 3")
                            .description("Terminator 3: Rise of the Machines is a 2003 science fiction action film " +
                                    "directed by Jonathan Mostow. Starring Arnold Schwarzenegger, Nick Stahl, " +
                                    "Claire Danes, and Kristanna Loken, it is the third installment in the Terminator" +
                                    "franchise and a sequel to Terminator 2: Judgment Day (1991). In its plot," +
                                    "the malevolent artificial intelligence Skynet sends a T-X (Loken)—a highly " +
                                    "advanced Terminator—back in time to ensure the rise of machines by killing " +
                                    "top members of the future human resistance as John Connor's (Stahl) location " +
                                    "is unknown. The resistance sends back a reprogrammed T-101 (Schwarzenegger) " +
                                    "to protect John and Kate.")
                            .releaseDate(LocalDate.of(2003, 6, 30))
                            .duration(109)
                            .build();
                    filmController.addFilm(newFilm);
                });
        assertEquals(exception.getMessage(), "400 BAD_REQUEST \"Film description may not exceed 200 symbols\"");
    }

    @Test
    public void testFailInvalidReleaseDate() {
        final ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> {
                    Film newFilm = Film.builder()
                            .name("Terminator 4")
                            .description("Terminator Salvation")
                            .releaseDate(LocalDate.of(1809, 5, 21))
                            .build();
                    filmController.addFilm(newFilm);
                });
        assertEquals(exception.getMessage(), "400 BAD_REQUEST \"Invalid film release date\"");
    }

    @Test
    public void testFailInvalidDuration() {
        final ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () ->
                {
                    Film newFilm = Film.builder()
                            .name("Terminator 5")
                            .description("Terminator Genisys")
                            .releaseDate(LocalDate.of(2015, 7, 2))
                            .duration(-126)
                            .build();
                    filmController.addFilm(newFilm);
                });
        assertEquals(exception.getMessage(), "400 BAD_REQUEST \"Duration must be positive number\"");
    }

    @Test
    public void testUserControllerSuccess() {
        User newUser = User.builder()
                .name(Optional.of("Sarah Connor"))
                .login("SConnor")
                .birthday(LocalDate.of(1984, 10, 26))
                .email("sconnor@gmail.com")
                .build();
        ResponseEntity<User> result = userController.addUser(newUser);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(1, userStorage.getAllUsers().size());
    }

    @SneakyThrows
    @Test
    public void testFailInvalidEmail() {
        String inValidEmailUser = "  \"login\": \"KReese\",\n" +
                "  \"name\": \"Kyle Reese\",\n" +
                "  \"email\": \"terminatormail@\",\n" +
                "  \"birthday\": \"1980-08-20\"";
        mockMvc.perform(post("/users")
               .contentType("application/json")
               .content(inValidEmailUser)).andExpect(status().is4xxClientError());
    }

    @Test
    public void testFailEmptyLogin() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> User.builder()
                        .name(Optional.of("Terminator"))
                        .email("arnie@gmail.com")
                        .birthday(LocalDate.of(1947, 7, 30))
                        .build());
        assertEquals(exception.getMessage(), "login is marked non-null but is null");
    }

    @Test
    public void testFailInvalidBirthday() {
        final ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () ->
                {
                    User newUser = User.builder()
                            .name(Optional.of("T-1000"))
                            .login("T1000")
                            .email("t1000@gmail.com")
                            .birthday(LocalDate.of(2029, 1, 1))
                            .build();
                    userController.addUser(newUser);
                });
        assertEquals(exception.getMessage(), "400 BAD_REQUEST \"Birthday must not be a date in future\"");
    }

    @Test
    public void testUserControllerSuccessEmptyName() {
        User newUser = User.builder()
                .login("JohnConnor")
                .email("jconnor@gmail.com")
                .birthday(LocalDate.of(1985, 2, 28))
                .build();
        ResponseEntity<User> response = userController.addUser(newUser);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(2, userStorage.getAllUsers().size());
    }
}
