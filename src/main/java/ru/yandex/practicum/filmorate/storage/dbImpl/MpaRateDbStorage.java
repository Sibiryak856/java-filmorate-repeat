package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRate;
import ru.yandex.practicum.filmorate.storage.MpaRateStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaRateDbStorage implements MpaRateStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<MpaRate> findById(long id) {
        return jdbcTemplate.query(
                "SELECT * " +
                        "FROM mpa " +
                        "WHERE mpa_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id),
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(
                            makeMpa(rs, 1));
                });
    }

    @Override
    public List<MpaRate> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM mpa",
                this::makeMpa);
    }

    private MpaRate makeMpa(ResultSet rs, int i) throws SQLException {
        return MpaRate.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
