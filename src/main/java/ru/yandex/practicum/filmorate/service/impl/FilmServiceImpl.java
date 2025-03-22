package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRateStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {


    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaRateStorage mpaRateStorage;
    private final ValidateService<Film> validateService;

    @Override
    public Film create(Film film) {
        checkGenreAndMpaIsPresent(film);
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        checkGenreAndMpaIsPresent(film);
        getIfPresent(film.getId());
        filmStorage.update(film);
        return film;
    }

    @Override
    public Film getById(long id) {
        Film film = getIfPresent(id);
        film.setGenres(genreStorage.findAllByFilmId(id));
        return film;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = filmStorage.getAll();
        films.forEach(
                film -> film.setGenres(
                        genreStorage.findAllByFilmId(film.getId())));
        return films;
    }

    @Override
    public void updateLikes(long filmId, int userId, RequestMethod method) {
        getIfPresent(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        switch (method) {
            case PUT:
                filmStorage.addLike(userId, filmId);
                break;
            case DELETE:
                filmStorage.removeLike(userId, filmId);
                break;
            default:
                throw new NotFoundException("Unsupported method");
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted(new TopFilmsComparator())
                .peek(film -> film.setGenres(
                        genreStorage.findAllByFilmId(film.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }


    private Film getIfPresent(long id) {
        return validateService.getIfPresent(
                filmStorage.findById(id), Film.class.getSimpleName());
    }

    private void checkGenreAndMpaIsPresent(Film film) {
        mpaRateStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Film's mpa not found"));
        List<Genre> genres = film.getGenres();
        List<Genre> fromDb;
        if (genres != null && !genres.isEmpty()) {
            fromDb = genres.stream()
                    .map(genre -> genreStorage.findById(genre.getId())
                            .orElseThrow(() -> new NotFoundException("Genre not found")))
                    .collect(Collectors.toList());
            if (genres.size() != fromDb.size()) {
                throw new NotFoundException("Not all genres are found in the database");
            }
        }
    }

    private class TopFilmsComparator implements Comparator<Film> {

        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikes().size() - o1.getLikes().size();
        }
    }

}
