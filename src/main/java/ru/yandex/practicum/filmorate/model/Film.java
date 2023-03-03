package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Set<Integer> usersLiked;
    int id;
    @NonNull
    @NotBlank
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    Rating mpa;
    Set<Director> directors;
    LinkedHashSet<Genre> genres;
    List<Director> directors;

    public int getAmountOfLikes() {
        return usersLiked.size();
    }

    public void addLike(int userId) {
        usersLiked.add(userId);
    }

    public void removeLike(int userId) {
        if (!usersLiked.contains(userId)) {
            throw new UserDoesNotExistException("User does not exist");
        }
        usersLiked.remove(userId);
    }
}
