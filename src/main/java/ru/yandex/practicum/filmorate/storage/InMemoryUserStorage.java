package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage{

    private Map<Long, User> users;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
