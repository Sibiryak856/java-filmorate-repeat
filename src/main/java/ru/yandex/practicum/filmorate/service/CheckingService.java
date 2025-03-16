package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Optional;

@Data
@Component
public class CheckingService <T> {

    public T getIfPresent(Optional<T> t, String typeName) {
        return t.orElseThrow(() ->
                new NotFoundException(String.format("%S not found", typeName)));
    }
}
