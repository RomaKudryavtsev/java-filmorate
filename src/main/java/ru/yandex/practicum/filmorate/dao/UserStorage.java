package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User addUser(User user) throws UserAlreadyExistsException;

    User updateUser(User user) throws UserToBeUpdatedDoesNotExistException;

    User getUser(int userId);

    List<Event> getFeed(int userId);

    void deleteUser(int userId);

    void addToFriends(int userId, int friendId);

    void deleteFromFriends(int userId, int friendId);

    List<User> getListOfUserFriends(int userId);

    List<User> getListOfCommonFriends(int userId1, int userId2);

    List<Integer> getRecommendations(Integer userID);
}
