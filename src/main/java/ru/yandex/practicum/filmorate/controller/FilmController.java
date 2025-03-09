package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private FilmServiceImpl filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Request received: POST /films: {}", film);
        Film created = filmService.create(film);
        log.info("Request POST processed: {}", created);
        return created;
    }

    @PutMapping
    public void update(@Valid @RequestBody Film film) {
        log.info("Request received: PUT /films: {}", film);
        filmService.update(film);
        log.info("Request PUT processed");
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        log.info("Request received: GET /films/{}", id);
        Film film = filmService.getById(id);
        log.info("Request GET processed: {}", film);
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Request received: GET /films");
        List<Film> films = filmService.getAll();
        log.info("Request GET processed: {}", films);
        return films;
    }
}
