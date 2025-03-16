package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Request received: POST /users: {}", user);
        User created = service.create(user);
        log.info("Request POST processed: {}", created);
        return created;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Request received: PUT /users: {}", user);
        User updated = service.update(user);
        log.info("Request PUT processed");
        return updated;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        log.info("Request received: GET /users/{}", id);
        User user = service.getById(id);
        log.info("Request GET processed: {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Request received: GET /users");
        List<User> users = service.getAll();
        log.info("Request GET processed: {}", users);
        return users;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) {
        log.info("Request received: PUT /users/{}/friends/{}", id, friendId);
        service.updateFriendship(id, friendId, RequestMethod.PUT);
        log.info("Request PUT processed");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id,
                          @PathVariable Long friendId) {
        log.info("Request received: DELETE /users/{}/friends/{}", id, friendId);
        service.updateFriendship(id, friendId, RequestMethod.DELETE);
        log.info("Request DELETE processed");
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) {
        log.info("Request received: GET /users/{}/friends", id);
        List<User> users = service.getUserFriends(id);
        log.info("Request GET processed: {}", users);
        return users;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) {
        log.info("Request received: GET /users/{}/friends/common/{}", id, otherId);
        List<User> users = service.getCommonFriends(id, otherId);
        log.info("Request GET processed: {}", users);
        return users;
    }
}
