package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> usersData = new HashMap<>();
    private int userId = 0;

    @Override
    public List<User> getAllUsers() {
        return usersData.values().stream().collect(Collectors.toList());
    }

    @Override
    public User addUser(User user) throws UserAlreadyExistsException {
        if(usersData.containsKey(user.getId())) {
            throw new UserAlreadyExistsException();
        }
        ++userId;
        user.setId(userId);
        usersData.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) throws UserToBeUpdatedDoesNotExistException {
        if(!usersData.containsKey(user.getId())) {
            throw new UserToBeUpdatedDoesNotExistException();
        }
        usersData.put(user.getId(), user);
        return user;
    }
}
