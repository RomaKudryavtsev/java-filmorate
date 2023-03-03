package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class UserController {
    //NOTE: UserController is dependent from UserService
    private final UserService userService;
    private final static String GENERAL_FRIENDS_PATH = "/users/{id}/friends";
    private final static String ALTER_FRIENDS_PATH = "/users/{id}/friends/{friendId}";
    private final static String GENERAL_USERS_PATH = "/users";
    private final static String FEED_PATH = "/users/{id}/feed";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(FEED_PATH)
    public List<Event> getFeed(@PathVariable("id") Integer userId) {
        return userService.getFeed(userId);
    }

    @PutMapping(value = ALTER_FRIENDS_PATH)
    public void addToFriends(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping(value = ALTER_FRIENDS_PATH)
    public void deleteFromFriends(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping(GENERAL_FRIENDS_PATH)
    public List<User> getFriends(@PathVariable("id") Integer userId) {
        return userService.getListOfUsersFriends(userId);
    }

    @GetMapping(GENERAL_FRIENDS_PATH + "/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId1, @PathVariable("otherId") Integer userId2) {
        return userService.getListOfCommonFriends(userId1, userId2);
    }

    @GetMapping(GENERAL_USERS_PATH)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(GENERAL_USERS_PATH + "/{id}")
    public User getUserById(@PathVariable("id") Integer userId) {
        return userService.getUserById(userId);
    }

    @PostMapping(value = GENERAL_USERS_PATH)
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping(value = GENERAL_USERS_PATH)
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping(value = GENERAL_USERS_PATH + "/{id}")
    public void deleteUser(@PathVariable("id") Integer userId) {
        userService.deleteUserById(userId);
    }
}
