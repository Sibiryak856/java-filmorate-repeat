package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService service;

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Long id) {
        log.info("Request received: GET /genres/{}", id);
        Genre genre = service.findById(id);
        log.info("Request GET processed: {}", genre);
        return genre;
    }

    @GetMapping
    public List<Genre> getAll() {
        log.info("Request received: GET /genres");
        List<Genre> genres = service.findAll();
        log.info("Request GET processed: {}", genres);
        return genres;
    }

}
