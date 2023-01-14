package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private Set<Integer> usersLiked;
    private int id;
    @NonNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    public int getAmountOfLikes() {
        return usersLiked.size();
    }

    public void addLike(int userId) {
        usersLiked.add(userId);
    }

    public void removeLike(int userId) {
        if(!usersLiked.contains(userId)) {
            throw new UserDoesNotExistException("User does not exist");
        }
        usersLiked.remove(userId);}
}
