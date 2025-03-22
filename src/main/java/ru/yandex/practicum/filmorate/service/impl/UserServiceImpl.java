package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.CheckingService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;
    private final CheckingService<User> checkingService;

    @Override
    public User create(User user) {
        return storage.create(user);
    }

    @Override
    public User update(User user) {
        User updating = getIfPresent(user.getId());
        user.setFriends(updating.getFriends());
        storage.update(user);
        return user;
    }

    @Override
    public User getById(long id) {
        return getIfPresent(id);
    }

    @Override
    public List<User> getAll() {
        return storage.getAll();
    }

    @Override
    public void updateFriendship(long id, long friendId, RequestMethod method) {
        getIfPresent(id);
        getIfPresent(friendId);
        switch (method) {
            case PUT:
                storage.addFriend(id, friendId);
                break;
            case DELETE:
                storage.removeFriend(id, friendId);
                break;
            default:
                throw new NotFoundException("Unsupported method");
        }
    }

    @Override
    public List<User> getUserFriends(long id) {
        getIfPresent(id);
        return storage.getUserFriends(id);
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        getIfPresent(id);
        getIfPresent(otherId);
        List<User> userFriends = storage.getUserFriends(id);
        List<User> otherUserFriends = storage.getUserFriends(otherId);

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }

    private User getIfPresent(long id) {
        return checkingService.getIfPresent(storage.findById(id), User.class.getSimpleName());
    }

}
