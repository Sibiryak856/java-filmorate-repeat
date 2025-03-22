package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> findById(long id) {
        return jdbcTemplate.query(
                "SELECT * " +
                        "FROM genres " +
                        "WHERE genre_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id),
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(makeGenre(rs, 1));
                }
        );
    }

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM genres",
                this::makeGenre
        );
    }

    @Override
    public List<Genre> findAllByFilmId(long id) {
        return jdbcTemplate.query(
                "SELECT g.genre_id, g.genre_name " +
                        "FROM film_genres AS fg " +
                        "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                        "WHERE fg.film_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id),
                this::makeGenre);
    }

    private Genre makeGenre(ResultSet rs, int i) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
