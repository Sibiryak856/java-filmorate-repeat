package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    Director create(Director director);
    Director update(Director director);
    Director findById(long id);
    List<Director> findAll();

    void deleteById(long id);
}
