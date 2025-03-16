package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);
    void update(User user);
    Optional<User> findById(long id);
    List<User> getAll();
}
