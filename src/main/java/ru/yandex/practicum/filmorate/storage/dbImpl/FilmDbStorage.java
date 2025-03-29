package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
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
        updateDirectors(film.getId(), film.getDirectors());
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
        if (film.getDirectors() != null) {
            clearFilmDirectors(film.getId());
            updateDirectors(film.getId(), film.getDirectors());
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
        return jdbcTemplate.query(sql, this::makeFilm);
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

    @Override
    public List<Film> getFilmsByGenreAndYear(long genreId, int year) {
        return jdbcTemplate.query(
                "SELECT f.*, mpa.mpa_name " +
                        "FROM films as f " +
                        "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                        "JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                        "WHERE fg.genre_id = :genreId " +
                        "AND EXTRACT(YEAR FROM CAST(f.release_date AS date)) = :year",
                new MapSqlParameterSource()
                        .addValue("genreId", genreId)
                        .addValue("year", year),
                this::makeFilm);
    }

    @Override
    public List<Film> getFilmsByGenre(long genreId) {
        return jdbcTemplate.query(
                "SELECT f.*, mpa.mpa_name " +
                        "FROM films as f " +
                        "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                        "JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                        "WHERE fg.genre_id = :genreId",
                new MapSqlParameterSource()
                        .addValue("genreId", genreId),
                this::makeFilm);
    }

    @Override
    public List<Film> getFilmsByYear(int year) {
        return jdbcTemplate.query(
                "SELECT f.*, mpa.mpa_name " +
                        "FROM films as f " +
                        "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                        "WHERE EXTRACT(YEAR FROM CAST(f.release_date AS date)) = :year",
                new MapSqlParameterSource()
                        .addValue("year", year),
                this::makeFilm);
    }

    @Override
    public void deleteById(long filmId) {
        jdbcTemplate.update(
                "DELETE FROM films WHERE film_id = :filmId",
                new MapSqlParameterSource()
                        .addValue("filmId", filmId));
    }

    @Override
    public List<Film> getFilmByDirectorId(long directorId) {
        return jdbcTemplate.query(
                "SELECT f.*, mpa.mpa_name " +
                        "FROM films as f " +
                        "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                        "JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                        "WHERE fd.director_id = :directorId",
                new MapSqlParameterSource()
                        .addValue("directorId", directorId),
                this::makeFilm);
    }

    @Override
    public List<Film> getFilmByTitle(String query) {
        return jdbcTemplate.query(
                "SELECT f.*, mpa.mpa_name " +
                        "FROM films as f " +
                        "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                        "WHERE LOWER(f.film_name) LIKE :query",
                new MapSqlParameterSource()
                        .addValue("query", "%" + query + "%"),
                this::makeFilm);
    }

    @Override
    public List<Film> getFilmByDirectorName(String query) {
        return jdbcTemplate.query(
                "SELECT f.*, mpa.mpa_name " +
                        "FROM films as f " +
                        "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                        "JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                        "JOIN directors AS d on fd.director_id = d.director_id " +
                        "WHERE LOWER(d.director_name) LIKE :query",
                new MapSqlParameterSource()
                        .addValue("query", "%" + query + "%"),
                this::makeFilm);
    }

    @Override
    public List<Film> getRecommendation(long id) {
        return jdbcTemplate.query(
                "SELECT f.*, m.*\n" +
                        "FROM FILMS f\n" +
                        "JOIN MPA m ON m.MPA_ID = f.MPA_ID \n" +
                        "JOIN LIKES l ON l.FILM_ID  = f.FILM_ID \n" +
                        "WHERE L.USER_ID IN (\n" +
                        "SELECT L2.USER_ID\n" +
                        "FROM LIKES AS L2\n" +
                        "WHERE L2.FILM_ID IN " +
                        "(" +
                        "SELECT L3.FILM_ID \n" +
                        "FROM LIKES L3 \n" +
                        "WHERE L3.USER_ID = :id)" +
                        ")\n" +
                        "AND f.FILM_ID NOT IN " +
                        "(" +
                        "SELECT L3.FILM_ID \n" +
                        "FROM LIKES L3 \n" +
                        "WHERE L3.USER_ID = :id\n" +
                        ")\n" +
                        "GROUP BY f.FILM_ID \n" +
                        "ORDER BY COUNT(*) DESC\n" +
                        "LIMIT 10",
                new MapSqlParameterSource()
                        .addValue("id", id),
                this::makeFilm
        );
    }

    private void updateGenres(long filmId, List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
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
                    "MERGE INTO film_genres (film_id, genre_id) VALUES (:filmId, :genreId)",
                    params);
        }
    }

    private void updateDirectors(long filmId, List<Director> directors) {
        if (directors != null && !directors.isEmpty()) {
            List<Director> newDirectors = new ArrayList<>(directors);
            Map[] valueMaps = new Map[newDirectors.size()];
            for (int i = 0; i < newDirectors.size(); i++) {
                Map<String, Long> map = new HashMap<>();
                map.put("filmId", filmId);
                map.put("directorId", directors.get(i).getId());
                valueMaps[i] = map;
            }
            SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(valueMaps);

            jdbcTemplate.batchUpdate(
                    "MERGE INTO film_directors (film_id, director_id) VALUES (:filmId, :directorId)",
                    params);
        }
    }

    private void clearFilmGenres(long id) {
        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = :id",
                new MapSqlParameterSource().addValue("id", id));
    }

    private void clearFilmDirectors(Long id) {
        jdbcTemplate.update(
                "DELETE FROM film_directors WHERE film_id = :id",
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
