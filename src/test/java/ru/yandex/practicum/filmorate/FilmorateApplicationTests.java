package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void testPostUserSuccess() {
        User newUser = User.builder()
                .name("Sarah Connor")
                .login("SConnor")
                .birthday(LocalDate.of(1984, 10, 26))
                .email("sconnor@gmail.com")
                .build();
        String newUserString = objectMapper.writeValueAsString(newUser);
        mockMvc.perform(post("/users").contentType("application/json").content(newUserString))
                .andDo(h -> System.out.println(h.getResponse().getContentAsString()))
                .andExpect(status().is2xxSuccessful());
        
    }

    @SneakyThrows
    @Test
    void testPostUserFailInvalidEmail() {
        User newUser = User.builder()
                .login("KReese")
                .name("Kyle Reese")
                .email("terminatormail@")
                .birthday(LocalDate.of(1980, 8,20))
                .build();
        String newUserString = objectMapper.writeValueAsString(newUser);
        mockMvc.perform(post("/users").contentType("application/json").content(newUserString))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().is5xxServerError());

    }

    @SneakyThrows
    @Test
    void testPostUserSuccessEmptyName() {
        User newUser = User.builder()
                .login("JohnConnor")
                .email("jconnor@gmail.com")
                .birthday(LocalDate.of(1985, 2, 28))
                .build();
        String newUserString = objectMapper.writeValueAsString(newUser);
        mockMvc.perform(post("/users").contentType("application/json").content(newUserString))
                .andDo(h -> System.out.println(h.getResponse().getContentAsString()))
                .andExpect(status().is2xxSuccessful());
        
    }

    @SneakyThrows
    @Test
    void testPostUserFailInvalidBirthday() {
        User newUser = User.builder()
                .name("T-1000")
                .login("T1000")
                .email("t1000@gmail.com")
                .birthday(LocalDate.of(2029, 1, 1))
                .build();
        String newUserString = objectMapper.writeValueAsString(newUser);
        mockMvc.perform(post("/users").contentType("application/json").content(newUserString))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testPostUserFailEmptyLogin() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> User.builder()
                        .name("Terminator")
                        .email("arnie@gmail.com")
                        .birthday(LocalDate.of(1947, 7, 30))
                        .build());
        assertEquals(exception.getMessage(), "login is marked non-null but is null");
    }

    @SneakyThrows
    @Test
    void testPostFilmSuccess() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(Genre.builder().id(1).build());
        Film newFilm = Film.builder()
                .name("Terminator")
                .usersLiked(new HashSet<>())
                .description("I'll be back!")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .mpa(Rating.builder().id(1).build())
                .genres(genres)
                .build();
        String newFilmString = objectMapper.writeValueAsString(newFilm);
        mockMvc.perform(post("/films").contentType("application/json").content(newFilmString))
                .andDo(h -> System.out.println(h.getResponse().getContentAsString()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testPostFilmFailEmptyName() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> Film.builder()
                        .description("Another great film.")
                        .releaseDate(LocalDate.of(1991, 7, 1))
                        .duration(137)
                        .build());
        assertEquals(exception.getMessage(), "name is marked non-null but is null");
    }

    @SneakyThrows
    @Test
    void testPostFilmFailInvalidDescription() {
        Film newFilm = Film.builder()
                .name("Terminator 3")
                .usersLiked(new HashSet<>())
                .description("Terminator 3: Rise of the Machines is a 2003 science fiction action film " +
                        "directed by Jonathan Mostow. Starring Arnold Schwarzenegger, Nick Stahl, " +
                        "Claire Danes, and Kristanna Loken, it is the third installment in the Terminator" +
                        "franchise and a sequel to Terminator 2: Judgment Day (1991). In its plot," +
                        "the malevolent artificial intelligence Skynet sends a T-X (Loken) -a highly " +
                        "advanced Terminator -back in time to ensure the rise of machines by killing " +
                        "top members of the future human resistance as John Connor's (Stahl) location " +
                        "is unknown. The resistance sends back a reprogrammed T-101 (Schwarzenegger) " +
                        "to protect John and Kate.")
                .releaseDate(LocalDate.of(2003, 6, 30))
                .duration(109)
                .build();
        String newFilmString = objectMapper.writeValueAsString(newFilm);
        mockMvc.perform(post("/films").contentType("application/json").content(newFilmString))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testPostFilmFailInvalidReleaseDate() {
        Film newFilm = Film.builder()
                .name("Terminator 4")
                .usersLiked(new HashSet<>())
                .description("Terminator Salvation")
                .releaseDate(LocalDate.of(1809, 5, 21))
                .build();
        String newFilmString = objectMapper.writeValueAsString(newFilm);
        mockMvc.perform(post("/films").contentType("application/json").content(newFilmString))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testPostFilmFailInvalidDuration() {
        Film newFilm = Film.builder()
                .name("Terminator 5")
                .usersLiked(new HashSet<>())
                .description("Terminator Genisys")
                .releaseDate(LocalDate.of(2015, 7, 2))
                .duration(-126)
                .build();
        String newFilmString = objectMapper.writeValueAsString(newFilm);
        mockMvc.perform(post("/films").contentType("application/json").content(newFilmString))
                .andDo(h -> System.out.println(h.getResponse().getStatus()))
                .andExpect(status().is4xxClientError());
    }
}
