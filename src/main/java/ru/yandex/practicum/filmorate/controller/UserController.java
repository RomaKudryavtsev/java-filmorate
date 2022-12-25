package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@Slf4j
public class UserController {
    private final UserStorage storage;

    public UserController() {
        storage = new InMemoryUserStorage();
    }

    public UserController(UserStorage storage) {
        this.storage = storage;
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
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    @PostMapping(value = "/users")
    @ResponseBody
    public ResponseEntity<User> addUser (@Valid @RequestBody User user) {
        try {
            validateUser(user);
            if(user.getName() == null || !user.getName().isPresent()) {
                user.setName(Optional.of(user.getLogin()));
            }
            User addedUser = storage.addUser(user);
            log.info("The following user was successfully added: {}", addedUser);
            return new ResponseEntity<User>(addedUser, HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            log.debug("The user already exists");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user already exists");
        } catch (ValidationException e) {
            log.debug(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping(value = "/users")
    @ResponseBody
    public ResponseEntity<User> updateUser (@Valid @RequestBody User user) {
        try {
            storage.updateUser(user);
            log.info("The following user was successfully updated: {}", user);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (UserToBeUpdatedDoesNotExistException e) {
            log.debug("The user to be updated does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user to be updated does not exist");
        }
    }
}
