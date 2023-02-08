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
    Set<Friendship> usersFriends;
    int id;
    @Email
    String email;
    @NonNull
    String login;
    String name;
    LocalDate birthday;

    public void addFriend(Friendship friendship) {
        usersFriends.add(friendship);
    }

    public void removeFriend(Friendship friendship) {
        if(!usersFriends.contains(friendship)) {
            throw new UserDoesNotExistException("User does not exist");
        }
        usersFriends.remove(friendship);}
}
