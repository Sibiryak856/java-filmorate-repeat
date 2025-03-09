package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private UserServiceImpl userService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Request received: POST /users: {}", user);
        User created = userService.create(user);
        log.info("Request POST processed: {}", created);
        return created;
    }

    @PutMapping
    public void update(@Valid @RequestBody User user) {
        log.info("Request received: PUT /users: {}", user);
        userService.update(user);
        log.info("Request PUT processed");
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        log.info("Request received: GET /users/{}", id);
        User user = userService.getById(id);
        log.info("Request GET processed: {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Request received: GET /users");
        List<User> users = userService.getAll();
        log.info("Request GET processed: {}", users);
        return users;
    }
}
