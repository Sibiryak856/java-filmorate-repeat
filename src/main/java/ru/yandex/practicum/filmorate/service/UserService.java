package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User create(User user);
    void update(User user);
    User getById(long id);
    List<User> getAll();
}
