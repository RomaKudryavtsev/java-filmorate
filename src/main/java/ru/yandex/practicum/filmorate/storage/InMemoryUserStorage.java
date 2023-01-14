package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.UserToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> usersData = new HashMap<>();
    private int userId = 0;

    private boolean validateUser(User user) throws ValidationException {
        if(user.getLogin().contains(" ")) {
            throw new ValidationException("Login must not contain white-spaces");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday must not be a date in future");
        }
        return true;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(usersData.values());
    }

    @Override
    public User addUser(User user) throws UserAlreadyExistsException {
        if(usersData.containsKey(user.getId())) {
            throw new UserAlreadyExistsException("This user already exists");
        }
        validateUser(user);
        if(user.getName() == null || user.getName().isEmpty()) {
            user.setName((user.getLogin()));
        }
        if (user.getUsersFriends() == null) {
            user.setUsersFriends(new HashSet<>());
        }
        ++userId;
        user.setId(userId);
        usersData.put(user.getId(), user);
        log.info("The following user was successfully added: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) throws UserToBeUpdatedDoesNotExistException {
        if(!usersData.containsKey(user.getId())) {
            throw new UserToBeUpdatedDoesNotExistException("User to be updated does not exist");
        }
        Set<Integer> usersFriendsBeforeUpdate = usersData.get(user.getId()).getUsersFriends();
        user.setUsersFriends(usersFriendsBeforeUpdate);
        usersData.put(user.getId(), user);
        log.info("The following user was successfully updated: {}", user);
        return user;
    }

    @Override
    public User getUser(int userId) throws UserDoesNotExistException {
        if(!usersData.containsKey(userId)) {
            throw new UserDoesNotExistException("This user does not exist");
        }
        return usersData.get(userId);
    }
}
