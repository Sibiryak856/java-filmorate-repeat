package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        director.setId(insert.executeAndReturnKey(toMap(director)).longValue());
        return director;
    }

    @Override
    public void update(Director director) {
        jdbcTemplate.update(
                "UPDATE directors SET " +
                        "director_name = :name",
                new MapSqlParameterSource()
                        .addValue("name", director.getName()));
    }

    @Override
    public Optional<Director> findById(long id) {
        return jdbcTemplate.query(
                "SELECT * " +
                        "FROM directors " +
                        "WHERE director_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id),
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(makeDirector(rs, 1));
                }
        );
    }

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query(
                "SELECT * " +
                        "FROM directors",
                this::makeDirector);
    }

    @Override
    public List<Director> findAllByFilmId(long id) {
        return jdbcTemplate.query(
                "SELECT d.* " +
                        "FROM film_directors AS fd " +
                        "JOIN directors AS d ON fd.director_id = d.director_id " +
                        "WHERE fd.film_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id),
                this::makeDirector);
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update(
                "DELETE FROM directors " +
                        "WHERE director_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id)
        );
    }

    private Director makeDirector(ResultSet rs, int i) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }

    private Map<String, Object> toMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("director_name", director.getName());
        return values;
    }
}
