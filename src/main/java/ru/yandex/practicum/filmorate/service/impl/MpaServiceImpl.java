package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRate;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaRateStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaRateStorage storage;

    @Override
    public MpaRate findById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("MPA id=%d not found", id)));
    }

    @Override
    public List<MpaRate> findAll() {
        return storage.findAll();
    }
}
