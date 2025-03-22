package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("userDb")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public User create(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(insert.executeAndReturnKey(user.toMap()).longValue());
        return user;
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update(
                "UPDATE users " +
                        "SET email = ?, login = ?, user_name = ?, birthday = ? " +
                        "WHERE user_id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM users WHERE user_id = ?",
                        this::makeUser,
                        id)
        );
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
                "INSERT INTO user_friends (user_id, fiend_id) VALUES(?, ?)",
                userId,
                friendId
        );
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbcTemplate.update(
                "DELETE FROM likes WHERE user_id = ? AND friend_id = ?",
                userId,
                friendId
        );
    }

    @Override
    public List<User> getUserFriends(long userId) {
        String sql = "SELECT u.* " +
                "FROM users AS u " +
                "JOIN user_friends AS uf ON u.user_id = uf.user_id " +
                "WHERE u.user_id = ?";
        return jdbcTemplate.query(sql, this::makeUser);
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
}
