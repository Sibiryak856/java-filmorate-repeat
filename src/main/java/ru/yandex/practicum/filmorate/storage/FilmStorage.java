package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);
    void update(Film film);
    Optional<Film> findById(long id);
    List<Film> getAll();
    void addLike(long filmId, long userId);
    void removeLike(long filmId, long userId);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> getFilmsByGenreAndYear(long genreId, int year);

    List<Film> getFilmsByGenre(long genreId);

    List<Film> getFilmsByYear(int year);
}
