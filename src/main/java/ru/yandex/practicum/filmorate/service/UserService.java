package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User create(User user);
    void update(User user);
    User getById(long id);
    List<User> getAll();

    void updateFriendship(long id, long friendId, RequestMethod method);

    List<User> getUserFriends(long id);

    List<User> getCommonFriends(long id, long otherId);
}
