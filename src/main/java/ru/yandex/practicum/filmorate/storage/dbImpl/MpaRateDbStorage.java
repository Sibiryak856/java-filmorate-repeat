package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.MpaRateStorage;
import ru.yandex.practicum.filmorate.model.MpaRate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaRateDbStorage implements MpaRateStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<MpaRate> findById(long id) {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM mpa WHERE mpa_id = ?",
                        this::makeMpa,
                        id)
        );
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
