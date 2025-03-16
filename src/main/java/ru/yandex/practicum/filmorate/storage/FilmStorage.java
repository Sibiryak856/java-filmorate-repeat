package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);
    void update(Film film);
    Optional<Film> findById(long id);
    List<Film> getAll();
}
