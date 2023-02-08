package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.UserToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
//@Primary
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> usersData = new HashMap<>();
    private int userId = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(usersData.values());
    }

    @Override
    public User addUser(User user) throws UserAlreadyExistsException {
        ++userId;
        user.setId(userId);
        usersData.put(user.getId(), user);
        log.info("The following user was successfully added: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) throws UserToBeUpdatedDoesNotExistException {
        Set<Friendship> usersFriendsBeforeUpdate = usersData.get(user.getId()).getUsersFriends();
        user.setUsersFriends(usersFriendsBeforeUpdate);
        usersData.put(user.getId(), user);
        log.info("The following user was successfully updated: {}", user);
        return user;
    }

    @Override
    public User getUser(int userId) throws UserDoesNotExistException {
        return usersData.get(userId);
    }

    @Override
    public void deleteUser(int userId) throws UserDoesNotExistException {
        usersData.remove(userId);
    }

    @Override
    public void addToFriends(int userId, int friendId) {
        //NOTE: If user already has this friend - we have to confirm friendship.
        //Otherwise, new non-confirmed friendship will be created only for user with userId.
        Set<Integer> friendsOfUserWithFriendId = this.getUser(friendId).getUsersFriends()
                .stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
        if(!friendsOfUserWithFriendId.contains(userId)) {
            this.getUser(userId).addFriend(Friendship.builder()
                    .friendId(friendId)
                    .confirmed(false)
                    .build());
            log.info("User {} send request for friendship to user {}. User {} is now non-confirmed friend of user {}.",
                    userId, friendId, friendId, userId);
        } else {
            this.getUser(friendId).getUsersFriends()
                    .stream()
                    .filter(f -> f.getFriendId() == userId)
                    .forEach(f -> f.setConfirmed(true));
            this.getUser(userId).addFriend(Friendship.builder()
                    .friendId(friendId)
                    .confirmed(true)
                    .build());
            log.info("Now users {} and {} are confirmed friends", userId, friendId);
        }
    }

    //NOTE: Friendship has to be deleted for both users (regardless of whether it is confirmed).
    @Override
    public void deleteFromFriends(int userId, int friendId) {
        User user = this.getUser(userId);
        User friend = this.getUser(friendId);
        user.removeFriend(user.getUsersFriends().stream()
                .filter(f -> f.getFriendId() == friendId)
                .findFirst().orElseThrow(() -> {throw new UserDoesNotExistException();}));
        Optional<Friendship> optionalFriendship = friend.getUsersFriends().stream()
                .filter(f -> f.getFriendId() == userId)
                .findFirst();
        optionalFriendship.ifPresent(user::removeFriend);
    }

    @Override
    public List<User> getListOfUserFriends(int userId) {
        Set<Integer> usersFriendIds = this.getUser(userId).getUsersFriends().stream()
                .map(Friendship::getFriendId).collect(Collectors.toSet());
        return this.getAllUsers().stream()
                .filter(user -> usersFriendIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getListOfCommonFriends(int userId1, int userId2) {
        Set<Integer> usersFriendsId1 = this.getUser(userId1).getUsersFriends().stream()
                .map(Friendship::getFriendId).collect(Collectors.toSet());
        Set<Integer> commonFriends = this.getUser(userId2).getUsersFriends().stream()
                .map(Friendship::getFriendId)
                .filter(usersFriendsId1::contains).collect(Collectors.toSet());
        return this.getAllUsers().stream().filter(u -> commonFriends.contains(u.getId()))
                .collect(Collectors.toList());
    }
}
