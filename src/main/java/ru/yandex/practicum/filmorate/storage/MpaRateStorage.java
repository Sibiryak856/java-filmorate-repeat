package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRate;

import java.util.List;
import java.util.Optional;

public interface MpaRateStorage {

    Optional<MpaRate> findById(long id);
    List<MpaRate> findAll();

}
