package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRate;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService service;

    @GetMapping("/{id}")
    public MpaRate getById(@PathVariable Long id) {
        log.info("Request received: GET /mpa/{}", id);
        MpaRate mpaRate = service.findById(id);
        log.info("Request GET processed: {}", mpaRate);
        return mpaRate;
    }

    @GetMapping
    public List<MpaRate> getAll() {
        log.info("Request received: GET /mpa");
        List<MpaRate> mpas = service.findAll();
        log.info("Request GET processed: {}", mpas);
        return mpas;
    }
}
