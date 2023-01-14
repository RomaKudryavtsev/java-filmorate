package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Set<Integer> usersFriends;
    int id;
    @Email
    String email;
    @NonNull
    String login;
    String name;
    LocalDate birthday;

    public void addFriend(int friendId) {
        usersFriends.add(friendId);
    }

    public void removeFriend(int friendId) {
        if(!usersFriends.contains(friendId)) {
            throw new UserDoesNotExistException("User does not exist");
        }
        usersFriends.remove(friendId);}
}
