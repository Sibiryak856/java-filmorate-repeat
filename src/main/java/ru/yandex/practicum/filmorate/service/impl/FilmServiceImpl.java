package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {


    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaRateStorage mpaRateStorage;
    private final DirectorStorage directorStorage;
    private final ValidateService<Film> validateService;

    @Override
    public Film create(Film film) {
        checkFieldDataIsPresent(film);
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        checkFieldDataIsPresent(film);
        getIfPresent(film.getId());
        filmStorage.update(film);
        return film;
    }

    @Override
    public Film getById(long id) {
        Film film = getIfPresent(id);
        setGenreAndDirector(film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = filmStorage.getAll();
        films.forEach(this::setGenreAndDirector);
        return films;
    }

    @Override
    public void updateLikes(long filmId, long userId, RequestMethod method) {
        getIfPresent(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        switch (method) {
            case PUT:
                filmStorage.addLike(filmId, userId);
                break;
            case DELETE:
                filmStorage.removeLike(filmId, userId);
                break;
            default:
                throw new NotFoundException("Unsupported method");
        }
    }

    @Override
    public List<Film> getPopular(int count, long genreId, int year) {
        if (genreId != 0 && year != 0) {
            return getSortedFilms(filmStorage.getFilmsByGenreAndYear(genreId, year), count);
        } else if (genreId != 0) {
            return getSortedFilms(filmStorage.getFilmsByGenre(genreId), count);
        } else if (year != 0) {
            return getSortedFilms(filmStorage.getFilmsByYear(year), count);
        } else {
            return getSortedFilms(filmStorage.getAll(), count);
        }
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return getSortedFilms(filmStorage.getCommonFilms(userId, friendId), null);
    }

    @Override
    public void deleteById(long filmId) {
        filmStorage.deleteById(filmId);
    }

    @Override
    public List<Film> getFilmByDirectorId(long directorId, String sortBy) {
        List<Film> films = filmStorage.getFilmByDirectorId(directorId);
        switch (sortBy) {
            case "year":
                return films.stream()
                        .sorted(Comparator.comparing(Film::getReleaseDate))
                        .peek(this::setGenreAndDirector)
                        .collect(Collectors.toList());
            case "likes":
                return getSortedFilms(films, null);
            default:
                throw new NotFoundException(String.format("Unsupported sortBy params %s", sortBy));
        }
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String lowQuery = query.toLowerCase();
        Set<Film> films = new HashSet<>();
        if (by.contains("title") && by.contains("director")) {
            films.addAll(filmStorage.getFilmByDirectorName(lowQuery));
            films.addAll(filmStorage.getFilmByTitle(lowQuery));

            return getSortedFilms(films, null);
        } else if (by.contains("title")) {
            return getSortedFilms(filmStorage.getFilmByTitle(lowQuery), null);
        } else if  (by.contains("director")) {
            return getSortedFilms(filmStorage.getFilmByDirectorName(lowQuery), null);
        } else {
            throw new NotFoundException("Unsupported request query");
        }
    }

    private List<Film> getSortedFilms(Collection<Film> films, @Nullable Integer count) {
        if (count == null) {
            return films.stream()
                    .peek(this :: setGenreAndDirector)
                    .sorted(new TopFilmsComparator())
                    .collect(Collectors.toList());
        }
        return films.stream()
                .peek(this :: setGenreAndDirector)
                .sorted(new TopFilmsComparator())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void setGenreAndDirector(Film film) {
        film.setGenres(
                genreStorage.findAllByFilmId(film.getId()));
        film.setDirectors(
                directorStorage.findAllByFilmId(film.getId()));
    }

    private Film getIfPresent(long id) {
        return validateService.getIfPresent(
                filmStorage.findById(id), Film.class.getSimpleName());
    }

    private void checkFieldDataIsPresent(Film film) {
        mpaRateStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Film's mpa not found"));
        List<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            directors.forEach(director -> directorStorage.findById(director.getId())
                    .orElseThrow(() ->
                            new NotFoundException(String.format("Director id=: %d not found", director.getId())))
            );
        }
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
