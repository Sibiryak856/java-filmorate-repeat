package ru.yandex.practicum.filmorate.storage.memImpl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component("memory")
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    @Override
    public Film create(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(long userId, long filmId) {

    }

    @Override
    public void removeLike(long userId, long filmId) {

    }
}
