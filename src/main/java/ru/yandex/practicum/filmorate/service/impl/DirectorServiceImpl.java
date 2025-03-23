package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;
    private final ValidateService<Director> service;


    @Override
    public Director create(Director director) {
        return directorStorage.create(director);
    }

    @Override
    public Director update(Director director) {
        service.getIfPresent(
                directorStorage.findById(director.getId()), Director.class.getSimpleName());
        directorStorage.update(director);
        return director;
    }

    @Override
    public Director findById(long id) {
        return service.getIfPresent(
                directorStorage.findById(id), Director.class.getSimpleName());
    }

    @Override
    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    @Override
    public void deleteById(long id) {
        directorStorage.deleteById(id);
    }
}
