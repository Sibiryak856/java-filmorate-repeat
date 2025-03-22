package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Request received: POST /films: {}", film);
        Film created = service.create(film);
        log.info("Request POST processed: {}", created);
        return created;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Request received: PUT /films: {}", film);
        Film updated = service.update(film);
        log.info("Request PUT processed");
        return updated;
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        log.info("Request received: GET /films/{}", id);
        Film film = service.getById(id);
        log.info("Request GET processed: {}", film);
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Request received: GET /films");
        List<Film> films = service.getAll();
        log.info("Request GET processed: {}", films);
        return films;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        log.info("Request received: PUT /films/{}/like/{}", id, userId);
        service.updateLikes(id, userId, RequestMethod.PUT);
        log.info("Request PUT processed");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        log.info("Request received: DELETE /films/{}/like/{}", id, userId);
        service.updateLikes(id, userId, RequestMethod.DELETE);
        log.info("Request DELETE processed");
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam (defaultValue = "10", required = false) @Positive Integer count,
            @RequestParam (defaultValue = "0") Long genreId,
            @RequestParam (defaultValue = "0") Integer year) {
        log.info("Request received: Get /films/popular?count={}&genreId={}&year={}", count, genreId, year);
        List<Film> films = service.getPopular(count, genreId, year);
        log.info("Request GET processed");
        return films;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilmsByTwoUser(@RequestParam Long userId,
                                              @RequestParam Long friendId) {
        log.info("Request received: Get /films/common?userId={}&friendId={}", userId, friendId);
        List<Film> films = service.getCommonFilms(userId, friendId);
        log.info("Request GET processed");
        return films;
    }

}
