package ru.yandex.practicum.filmorate.service.impl;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.CheckingService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

class FilmServiceImplTest {

    private FilmStorage storage = new InMemoryFilmStorage();
    private FilmService service = new FilmServiceImpl();
    private Film film;
    private CheckingService<Film> checkingService = new CheckingService<>();

    @BeforeEach
    void init() {
        film = Film.builder()
                .id(1L)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now().minusDays(1))
                .duration(100)
                .build();
    }

    /*@Test
    void checkIfPresent() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> checkingService.getIfPresent(Optional.ofNullable(null), Film.class.getSimpleName()));
        assertEquals("", e.getMessage());

    }*/

}