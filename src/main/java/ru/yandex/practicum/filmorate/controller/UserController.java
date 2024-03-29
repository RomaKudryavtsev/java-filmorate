package ru.yandex.practicum.filmorate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@Api(tags = "Users")
public class UserController {
    private final UserService userService;
    private final static String GENERAL_USERS_PATH = "/users";
    private final static String GENERAL_FRIENDS_PATH = GENERAL_USERS_PATH + "/{id}/friends";
    private final static String ALTER_FRIENDS_PATH = GENERAL_FRIENDS_PATH + "/{friendId}";
    private final static String FEED_PATH = GENERAL_USERS_PATH + "/{id}/feed";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Get feed")
    @GetMapping(FEED_PATH)
    public List<Event> getFeed(@PathVariable("id") Integer userId) {
        return userService.getFeed(userId);
    }

    @ApiOperation(value = "Add to friends")
    @PutMapping(value = ALTER_FRIENDS_PATH)
    public void addToFriends(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.addToFriends(id, friendId);
    }

    @ApiOperation(value = "Delete from friends")
    @DeleteMapping(value = ALTER_FRIENDS_PATH)
    public void deleteFromFriends(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.deleteFromFriends(userId, friendId);
    }

    @ApiOperation(value = "Get friends")
    @GetMapping(GENERAL_FRIENDS_PATH)
    public List<User> getFriends(@PathVariable("id") Integer userId) {
        return userService.getListOfUsersFriends(userId);
    }

    @ApiOperation(value = "Get common friends")
    @GetMapping(GENERAL_FRIENDS_PATH + "/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId1, @PathVariable("otherId") Integer userId2) {
        return userService.getListOfCommonFriends(userId1, userId2);
    }

    @ApiOperation(value = "Get all users")
    @GetMapping(GENERAL_USERS_PATH)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ApiOperation(value = "Get user by id")
    @GetMapping(GENERAL_USERS_PATH + "/{id}")
    public User getUserById(@PathVariable("id") Integer userId) {
        return userService.getUserById(userId);
    }

    @ApiOperation(value = "Add user")
    @PostMapping(value = GENERAL_USERS_PATH)
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @ApiOperation(value = "Update user")
    @PutMapping(value = GENERAL_USERS_PATH)
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @ApiOperation(value = "Delete user")
    @DeleteMapping(value = GENERAL_USERS_PATH + "/{id}")
    public void deleteUser(@PathVariable("id") Integer userId) {
        userService.deleteUserById(userId);
    }

    @ApiOperation(value = "Get recommendations")
    @GetMapping(value = GENERAL_USERS_PATH + "/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable("id") Integer userID) {
        return userService.getRecommendations(userID);
    }
}
