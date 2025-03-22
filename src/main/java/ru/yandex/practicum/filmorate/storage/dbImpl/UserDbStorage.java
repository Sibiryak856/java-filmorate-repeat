package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository("userDb")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    @Override
    public User create(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(insert.executeAndReturnKey(toMap(user)).longValue());
        return user;
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update(
                "UPDATE users SET " +
                        "email = :email, " +
                        "login = :login, " +
                        "user_name = :user_name, " +
                        "birthday = :birthday " +
                        "WHERE user_id = :user_id",
                new MapSqlParameterSource(toMap(user)));
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query(
                "SELECT * " +
                        "FROM users " +
                        "WHERE user_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id),
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(
                            makeUser(rs, 1));
                });
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM users",
                this::makeUser
        );
    }

    @Override
    public void addFriend(long userId, long friendId) {
        jdbcTemplate.update(
                "MERGE INTO user_friends (user_id, friend_id) VALUES (:userId, :friendId)",
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("friendId", friendId));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbcTemplate.update(
                "DELETE FROM user_friends WHERE user_id = :userId AND friend_id = :friendId",
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("friendId", friendId));
    }

    @Override
    public List<User> getUserFriends(long userId) {
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE user_id IN (SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = :userId)";
        return jdbcTemplate.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId),
                this::makeUser);
    }

    @Override
    public void deleteById(long userId) {
        jdbcTemplate.update(
                "DELETE FROM users WHERE user_id = :userId",
                new MapSqlParameterSource()
                        .addValue("userId", userId));
    }


    private User makeUser(ResultSet rs, int i) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", user.getId());
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("user_name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }
}
