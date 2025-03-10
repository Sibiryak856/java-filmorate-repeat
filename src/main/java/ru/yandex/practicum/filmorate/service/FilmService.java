package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film create(Film film);
    void update(Film film);
    Film getById(long id);
    List<Film> getAll();
}
