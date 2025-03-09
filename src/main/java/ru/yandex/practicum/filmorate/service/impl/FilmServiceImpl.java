package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmServiceImpl implements FilmService {

    private Map<Long, Film> films;
    private long id = 0;

    public FilmServiceImpl() {
        this.films = new HashMap<>();
    }

    @Override
    public Film create(Film film) {
        id++;
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public void update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        films.put(film.getId(), film);
    }

    @Override
    public Film getById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film not found");
        }
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

}
