package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Director create(Director director);
    void update(Director director);
    Optional<Director> findById(long id);
    List<Director> findAll();

    List<Director> findAllByFilmId(long id);

    void deleteById(long id);
}
