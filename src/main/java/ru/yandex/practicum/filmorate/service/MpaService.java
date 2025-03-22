package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaRate;

import java.util.List;

public interface MpaService {

    MpaRate findById(long id);
    List<MpaRate> findAll();
}
