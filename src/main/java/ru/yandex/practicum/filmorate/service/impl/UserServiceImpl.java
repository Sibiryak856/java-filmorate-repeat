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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private long id = 0;
    private UserStorage storage;
    private CheckingService<User> checkingService;

    @Override
    public User create(User user) {
        id++;
        user.setId(id);
        return storage.create(user);
    }

    @Override
    public void update(User user) {
        User updating = getIfPresent(user.getId());
        storage.update(user);
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
        User addedFriends = getIfPresent(id);
        switch (method) {
            case PUT:
                user.addFriend(friendId);
                update(user);
                break;
            case DELETE:
                user.deleteFriend(friendId);
                update(user);
                break;
            default:
                throw new NotFoundException("Unsupported method");
        }
    }

    @Override
    public List<User> getUserFriends(long id) {
        Set<Long> friends = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getFriends();

        return friends.stream()
                .map(friendId -> storage.findById(friendId).get())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        User otherUser = storage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(friendId -> storage.findById(friendId).get())
                .collect(Collectors.toList());
    }

    private User getIfPresent(long id) {
        return checkingService.getIfPresent(
                storage.findById(id), User.class.getSimpleName());
    }

}
