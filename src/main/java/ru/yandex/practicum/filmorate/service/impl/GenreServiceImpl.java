package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreStorage storage;

    @Override
    public Genre findById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Genre id=%d not found", id)));
    }

    @Override
    public List<Genre> findAll() {
        return storage.findAll();
    }
}
