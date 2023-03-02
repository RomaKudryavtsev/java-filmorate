package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikesDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.UserToBeUpdatedDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@Primary
@Slf4j
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmLikesDao filmLikesDao;
    private final ReviewDao reviewDao;

    private final static String SELECT_ALL_INFO_ON_ALL_USERS_SQL = "select * from users";
    private final static String SELECT_ALL_INFO_ON_USER_SQL = "select * from users where user_id = ?";
    private final static String SELECT_USER_FRIENDSHIPS_SQL = "select f.friend_user_id AS user_id, f.confirmed " +
            "from friendship f where f.user_id = ? and f.confirmed = true " +
            "union " +
            "select f.user_id AS user_id, f.confirmed " +
            "from friendship f where f.friend_user_id = ? and f.confirmed = true " +
            "union " +
            "select f.user_id AS user_id, f.confirmed " +
            "from friendship f where f.friend_user_id = ? and f.confirmed = false";
    private final static String SELECT_USER_FRIEND_IDS_SQL = "select u.user_id from users u where u.user_id in " +
            "(select f.friend_user_id from friendship f where f.user_id = ? and f.confirmed = true " +
            "union " +
            "select f.user_id from friendship f where f.friend_user_id = ? and f.confirmed = true " +
            "union " +
            "select f.friend_user_id from friendship f where f.user_id = ? and f.confirmed = false)";
    private final static String SELECT_USER_FRIENDS_SQL = "select u.* from users u where u.user_id in " +
            "(select f.friend_user_id from friendship f where f.user_id = ? and f.confirmed = true " +
            "union " +
            "select f.user_id from friendship f where f.friend_user_id = ? and f.confirmed = true " +
            "union " +
            "select f.friend_user_id from friendship f where f.user_id = ? and f.confirmed = false)";
    private final static String SELECT_COMMON_FRIENDS_SQL = "select u.* from users u where u.user_id in " +
            "(select all_friends_for_pair.user_id from " +
            "(select f.user_id from friendship f where (f.friend_user_id = ? or f.friend_user_id = ?) " +
            "union all " +
            "select f.friend_user_id from friendship f where (f.user_id = ? or f.user_id = ?)) all_friends_for_pair " +
            "group by all_friends_for_pair.user_id having COUNT(*) > 1)";
    private final static String INSERT_NEW_USER_SQL = "insert into users (email, login, name, birthday) " +
            "values(?, ?, ?, ?)";
    private final static String INSERT_NEW_FRIENDSHIP_SQL = "insert into friendship (user_id, friend_user_id, confirmed) " +
            "VALUES (?, ?, ?)";
    private final static String UPDATE_USER_SQL = "update users set " +
            "email = ?, login = ?, name = ?, birthday = ? " +
            "where user_id = ?";
    private final static String UPDATE_FRIENDSHIP_SQL = "update friendship set confirmed = ? " +
            "where user_id = ? and friend_user_id = ?";
    private final static String DELETE_FRIENDSHIP_SQL = "delete from friendship where user_id = ? " +
            "and friend_user_id = ?";
    private final static String DELETE_ALL_USER_FRIENDSHIPS_SQL = "delete from friendship where user_id = ? " +
            "or friend_user_id = ?";
    private final static String DELETE_USER_SQL = "delete from users where user_id = ?";

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate, FilmLikesDao filmLikesDao, ReviewDao reviewDao) {
        this.filmLikesDao = filmLikesDao;
        this.jdbcTemplate = jdbcTemplate;
        this.reviewDao = reviewDao;
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(SELECT_ALL_INFO_ON_ALL_USERS_SQL, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User addUser(User user) throws UserAlreadyExistsException {
        KeyHolder userKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_NEW_USER_SQL, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, userKeyHolder);
        int userId = Objects.requireNonNull(userKeyHolder.getKey()).intValue();
        user.setId(userId);
        log.info("The following user was successfully added: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) throws UserToBeUpdatedDoesNotExistException {
        jdbcTemplate.update(UPDATE_USER_SQL
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , Date.valueOf(user.getBirthday())
                , user.getId());
        user.setUsersFriends(new HashSet<>(getListOfFriendshipsForUser(user.getId())));
        log.info("The following user was successfully updated: {}", user);
        return user;
    }

    @Override
    public User getUser(int userId) {
        return jdbcTemplate.query(SELECT_ALL_INFO_ON_USER_SQL, (rs, rowNum) -> makeUser(rs), userId)
                .stream().findFirst().orElseThrow(() -> {throw new UserDoesNotExistException();});
    }

    //NOTE: Upon deletion of user, his friendships, likes and reviews have to be deleted.
    @Override
    public void deleteUser(int userId) {
        jdbcTemplate.update(DELETE_ALL_USER_FRIENDSHIPS_SQL, userId, userId);
        filmLikesDao.deleteAllLikesOfUser(userId);
        reviewDao.deleteReviewsForUser(userId);
        jdbcTemplate.update(DELETE_USER_SQL, userId);
    }

    @Override
    public void addToFriends(int userId, int friendId) {
        //NOTE: If user already has this friend - we have to confirm friendship.
        //Otherwise, new non-confirmed friendship will be created only for user with userId.
        List<Integer> friendsOfUserWithFriendId = getListOfFriendIdsForUser(friendId);
        if(!friendsOfUserWithFriendId.contains(userId)) {
            jdbcTemplate.update(INSERT_NEW_FRIENDSHIP_SQL, userId, friendId, false);
            log.info("User {} send request for friendship to user {}. User {} is now non-confirmed friend of user {}.",
                    userId, friendId, friendId, userId);
        } else {
            jdbcTemplate.update(UPDATE_FRIENDSHIP_SQL, true, friendId, userId);
            log.info("Now users {} and {} are confirmed friends", userId, friendId);
        }
    }

    @Override
    public void deleteFromFriends(int userId, int friendId) {
        jdbcTemplate.update(DELETE_FRIENDSHIP_SQL, userId, friendId);
    }

    @Override
    public List<User> getListOfUserFriends(int userId) {
        return jdbcTemplate.query(SELECT_USER_FRIENDS_SQL, (rs, rowNum) -> makeUser(rs), userId, userId, userId);
    }

    @Override
    public List<User> getListOfCommonFriends(int userId1, int userId2) {
        return jdbcTemplate.query(SELECT_COMMON_FRIENDS_SQL, (rs, rowNum) -> makeUser(rs), userId1, userId2,
                userId1, userId2);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        User result = User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
        Set<Friendship> usersFriends = new HashSet<>(getListOfFriendshipsForUser(result.getId()));
        result.setUsersFriends(usersFriends);
        return result;
    }

    private List<Integer> getListOfFriendIdsForUser(int userId) {
        return jdbcTemplate.query(SELECT_USER_FRIEND_IDS_SQL, (rs, rowNum) -> makeUserId(rs),
                userId, userId, userId);
    }

    private List<Friendship> getListOfFriendshipsForUser(int userId) {
        return jdbcTemplate.query(SELECT_USER_FRIENDSHIPS_SQL, (rs, rowNum) -> makeFriendship(rs),
                userId, userId, userId);
    }

    private Friendship makeFriendship(ResultSet rs) throws SQLException {
        return Friendship.builder()
                .friendId(rs.getInt("user_id"))
                .confirmed(rs.getBoolean("confirmed"))
                .build();
    }

    private Integer makeUserId (ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }
}
