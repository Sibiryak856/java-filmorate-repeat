package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.CheckingService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {


    private long id = 0;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final CheckingService<Film> checkingService;

    @Override
    public Film create(Film film) {
        id++;
        film.setLikes(new HashSet<>());
        film.setId(id);
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        Film updating = getIfPresent(film.getId());
        film.setLikes(updating.getLikes());
        filmStorage.update(film);
        return film;
    }

    @Override
    public Film getById(long id) {
        return getIfPresent(id);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public void updateLikes(long id, int userId, RequestMethod method) {
        Film film = getIfPresent(id);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        switch (method) {
            case PUT:
                film.addLike(userId);
                filmStorage.update(film);
                break;
            case DELETE:
                film.deleteLike(userId);
                filmStorage.update(film);
                break;
            default:
                throw new NotFoundException("Unsupported method");
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted(new TopFilmsComparator())
                .limit(count)
                .collect(Collectors.toList());
    }


    private Film getIfPresent(long id) {
        return checkingService.getIfPresent(
                filmStorage.findById(id), Film.class.getSimpleName());
    }

    private class TopFilmsComparator implements Comparator<Film> {

        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikes().size() - o1.getLikes().size();
        }
    }

}
