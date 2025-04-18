package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film create(Film film);
    Film update(Film film);
    Film getById(long id);
    List<Film> getAll();

    void updateLikes(long id, long userId, RequestMethod requestMethod);

    List<Film> getPopular(int count, long genreId, int year);

    List<Film> getCommonFilms(long userId, long friendId);

    void deleteById(long filmId);

    List<Film> getFilmByDirectorId(long directorId, String sortBy);

    List<Film> searchFilms(String query, String by);

    List<Film> getRecommendation(long id);
}
