package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private Set<Integer> usersFriends;
    private int id;
    @Email
    private String email;
    @NonNull
    private String login;
    private String name;
    private LocalDate birthday;

    public void addFriend(int friendId) {
        usersFriends.add(friendId);
    }

    public void removeFriend(int friendId) {
        if(!usersFriends.contains(friendId)) {
            throw new UserDoesNotExistException("User does not exist");
        }
        usersFriends.remove(friendId);}
}
