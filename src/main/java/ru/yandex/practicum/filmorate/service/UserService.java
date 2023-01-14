package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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

    public void addToFriends(int userId, int friendId) {
        userStorage.getUser(userId).addFriend(friendId);
        userStorage.getUser(friendId).addFriend(userId);
        log.info("Now users {} and {} are friends", userId, friendId);
    }

    public void deleteFromFriends(int userId, int friendId) {
        userStorage.getUser(userId).removeFriend(friendId);
        userStorage.getUser(friendId).removeFriend(userId);
    }

    public List<User> getListOfUsersFriends(int userId) {
        Set<Integer> usersFriends = userStorage.getUser(userId).getUsersFriends();
        return userStorage.getAllUsers().stream().filter(u -> usersFriends.contains(u.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getListOfCommonFriends(int userId1, int userId2) {
        Set<Integer> usersFriends1 = userStorage.getUser(userId1).getUsersFriends();
        Set<Integer> commonFriends = userStorage.getUser(userId2).getUsersFriends().stream()
                .filter(id -> usersFriends1.contains(id)).collect(Collectors.toSet());
        return userStorage.getAllUsers().stream().filter(u -> commonFriends.contains(u.getId()))
                    .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        User addedUser = userStorage.addUser(user);
        return addedUser;
    }

    public User getUserById(int id) {
        return userStorage.getUser(id);
    }

    public User updateUser(User user) {
        userStorage.updateUser(user);
        return user;
    }
}
