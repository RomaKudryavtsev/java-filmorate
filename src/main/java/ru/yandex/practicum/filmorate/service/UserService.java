package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.UserToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    //NOTE: UserService is dependent from UserStorage
    private final UserStorage userStorage;

    @Autowired
    public UserService (UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private boolean validateUser(User user) throws ValidationException {
        if(user.getLogin().contains(" ")) {
            throw new ValidationException("Login must not contain white-spaces");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday must not be a date in future");
        }
        return true;
    }

    boolean checkIfUserExists(int userId) {
        Set<Integer> allCurrentUserIds = userStorage.getAllUsers().stream().map(User::getId)
                .collect(Collectors.toSet());
        if(!allCurrentUserIds.contains(userId)) {
            throw new UserDoesNotExistException("User or its friend does not exist");
        }
        return true;
    }

    public void addToFriends(int userId, int friendId) {
        checkIfUserExists(userId);
        checkIfUserExists(friendId);
        userStorage.addToFriends(userId, friendId);
    }

    public void deleteFromFriends(int userId, int friendId) {
        checkIfUserExists(userId);
        checkIfUserExists(friendId);
        userStorage.deleteFromFriends(userId, friendId);
    }

    public List<User> getListOfUsersFriends(int userId) {
        checkIfUserExists(userId);
        return userStorage.getListOfUserFriends(userId);
    }

    public List<User> getListOfCommonFriends(int userId1, int userId2) {
        checkIfUserExists(userId1);
        checkIfUserExists(userId2);
        return userStorage.getListOfCommonFriends(userId1, userId2);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        if(user.getName() == null || user.getName().isEmpty()) {
            user.setName((user.getLogin()));
        }
        if(user.getUsersFriends() == null) {
            user.setUsersFriends(new HashSet<>());
        }
        validateUser(user);
        if (userStorage.getAllUsers().stream().map(User::getId)
                .collect(Collectors.toSet()).contains(user.getId())){
            throw new UserAlreadyExistsException("This user already exists");
        }
        User addedUser = userStorage.addUser(user);
        return addedUser;
    }

    public User getUserById(int id) {
        checkIfUserExists(id);
        return userStorage.getUser(id);
    }

    public void deleteUserById(int id) {
        checkIfUserExists(id);
        userStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        if(!userStorage.getAllUsers().stream().map(User::getId)
                .collect(Collectors.toSet()).contains(user.getId())) {
            throw new UserToBeUpdatedDoesNotExistException("User to be updated does not exist");
        }
        userStorage.updateUser(user);
        return user;
    }
}
