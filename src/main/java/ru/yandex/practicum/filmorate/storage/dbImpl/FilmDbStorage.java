package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRate;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("filmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(insert.executeAndReturnKey(toMap(film)).longValue());
        updateGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public void update(Film film) {
        jdbcTemplate.update(
                "UPDATE films SET " +
                        "film_name = :film_name, " +
                        "description = :description, " +
                        "release_date = :release_date, " +
                        "duration = :duration, " +
                        "mpa_id = :mpa_id " +
                        "WHERE film_id = :id",
                new MapSqlParameterSource(toMap(film))
                        .addValue("id", film.getId()));
        if (film.getGenres() != null) {
            clearFilmGenres(film.getId());
            updateGenres(film.getId(), film.getGenres());
        }
    }

    @Override
    public Optional<Film> findById(long id) {
        return jdbcTemplate.query(
                "SELECT f.*, mpa.mpa_name " +
                        "FROM films AS f " +
                        "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                        "WHERE f.film_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id),
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(makeFilm(rs, 1));
                }
        );
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, mpa.mpa_name " +
                "FROM films as f " +
                "JOIN mpa ON f.mpa_id = mpa.mpa_id";
        return jdbcTemplate.query(sql, this :: makeFilm);
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(
                "INSERT INTO likes (user_id, film_id) VALUES(:userId, :filmId)",
                new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("userId", userId)
        );
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(
                "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId",
                new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("userId", userId)
        );
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return jdbcTemplate.query(
                "SELECT f.*, mpa.mpa_name " +
                        "FROM films as f " +
                        "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                        "JOIN likes AS l ON f.film_id = l.film_id " +
                        "WHERE l.user_id IN (:userId, :friendId) " +
                        "GROUP BY f.film_id " +
                        "HAVING COUNT(f.film_id) > 1",
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("friendId", friendId),
                this::makeFilm);
    }


    private void updateGenres(long filmId, List<Genre> genres) {
        if (genres != null && !genres.isEmpty() ) {
            List<Genre> genresList = new ArrayList<>(genres);
            Map[] valueMaps = new Map[genresList.size()];
            for (int i = 0; i < genresList.size(); i++) {
                Map<String, Long> map = new HashMap<>();
                map.put("filmId", filmId);
                map.put("genreId", genres.get(i).getId());
                valueMaps[i] = map;
            }
            SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(valueMaps);

            jdbcTemplate.batchUpdate(
                    "MERGE INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (:filmId, :genreId)",
                    params);
        }
    }

    private void clearFilmGenres(long id) {
        jdbcTemplate.update(
                "DELETE FROM FILM_GENRES WHERE FILM_ID = :id",
                new MapSqlParameterSource().addValue("id", id));
    }

    private Film makeFilm(ResultSet rs, int i) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MpaRate(
                        rs.getLong("mpa_id"),
                        rs.getString("mpa_name")))
                .build();
        setLikes(film);
        return film;
    }

    private void setLikes(Film film) {
        film.setLikes(
                new HashSet<>(jdbcTemplate.query(
                "SELECT user_id " +
                        "FROM likes " +
                        "WHERE film_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", film.getId()),
                ((rs1, rowNum) ->
                        rs1.getLong("user_id")))));
    }

    private Map<String, Object> toMap(Film fIlm) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", fIlm.getName());
        values.put("description", fIlm.getDescription());
        values.put("release_date", fIlm.getReleaseDate());
        values.put("duration", fIlm.getDuration());
        values.put("mpa_id", fIlm.getMpa().getId());
        return values;
    }

}
