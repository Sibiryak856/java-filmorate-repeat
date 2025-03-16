package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.CheckingService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private long id = 0;
    private final UserStorage storage;
    private final CheckingService<User> checkingService;

    @Override
    public User create(User user) {
        id++;
        user.setId(id);
        user.setFriends(new HashSet<>());
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
        User user = getIfPresent(id);
        User friend = getIfPresent(friendId);
        switch (method) {
            case PUT:
                user.addFriend(friendId);
                friend.addFriend(id);
                break;
            case DELETE:
                user.deleteFriend(friendId);
                friend.deleteFriend(id);
                break;
            default:
                throw new NotFoundException("Unsupported method");
        }
        storage.update(user);
        storage.update(friend);
    }

    @Override
    public List<User> getUserFriends(long id) {
        Set<Long> friends = getIfPresent(id).getFriends();

        return friends.stream()
                .map(friendId -> storage.findById(friendId).get())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        Set<Long> userFriends = getIfPresent(id).getFriends();
        Set<Long> otherUserFriends = getIfPresent(otherId).getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(friendId -> storage.findById(friendId).get())
                .collect(Collectors.toList());
    }

    private User getIfPresent(long id) {
        return checkingService.getIfPresent(storage.findById(id), User.class.getSimpleName());
    }

}
