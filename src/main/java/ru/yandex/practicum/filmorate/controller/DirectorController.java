package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Request received: POST /directors: {}", director);
        Director created = service.create(director);
        log.info("Request POST processed: {}", created);
        return created;
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Request received: PUT /directors: {}", director);
        Director updated = service.update(director);
        log.info("Request PUT processed: {}", updated);
        return updated;
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Long id) {
        log.info("Request received: GET /directors/{}", id);
        Director director = service.findById(id);
        log.info("Request GET processed: {}", director);
        return director;
    }

    @GetMapping
    public List<Director> getAll() {
        log.info("Request received: GET /directors");
        List<Director> directors = service.findAll();
        log.info("Request GET processed: {}", directors);
        return directors;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("Request received: DELETE /directors/{}", id);
        service.deleteById(id);
        log.info("Request DELETE processed:");
    }


}
