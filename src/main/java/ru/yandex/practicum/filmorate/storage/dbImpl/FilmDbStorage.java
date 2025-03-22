package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRate;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("filmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(insert.executeAndReturnKey(film.toMap()).longValue());
        updateGenres(film);
        return film;
    }

    @Override
    public void update(Film film) {
        String sql = "UPDATE films " +
                "SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        updateGenres(film);
    }

    @Override
    public Optional<Film> findById(long id) {
        String sql = "SELECT f.*, mpa.mpa_name " +
                "FROM films as f " +
                "JOIN mpa ON f.mpa_id = mpa.mpa_id" +
                "WHERE f.films_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this :: makeFilm, id));
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, mpa.mpa_name " +
                "FROM films as f " +
                "JOIN mpa ON f.mpa_id = mpa.mpa_id";
        return jdbcTemplate.query(sql, this :: makeFilm);
    }

    @Override
    public void addLike(long userId, long filmId) {
        jdbcTemplate.update(
                "INSERT INTO likes (user_id, film_id) VALUES(?, ?)",
                userId,
                filmId
        );
    }

    @Override
    public void removeLike(long userId, long filmId) {
        jdbcTemplate.update(
                "DELETE FROM likes WHERE user_id = ? AND film_id = ?",
                userId,
                filmId
        );
    }

    private void updateGenres(Film film) {
        List<Genre> genres = film.getGenres();
        if (genres.isEmpty()) {
            return;
        }
        String sql = "MERGE INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Genre genre = genres.get(i);
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genre.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                }
        );
    }

    private Film makeFilm(ResultSet rs, int i) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MpaRate(rs.getLong("mpa_id"), rs.getString("mpa_name")))
                .build();
        String queryForLikes = "SELECT user_id " +
                "FROM likes " +
                "WHERE film_id = ?";
        film.getLikes().addAll(
                jdbcTemplate.query(
                        queryForLikes,
                        ((rs1, rowNum) -> rs1.getLong("user_id")),
                        film.getId())
        );
        film.getGenres().addAll(findGenresByFilmId(film.getId()));
        return film;
    }

    private List<Genre> findGenresByFilmId(Long id) {
        String sql = "SELECT g.genre_id, g.genre_name " +
                "FROM film_genres AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, this::makeGenre, id);
    }

    private Genre makeGenre(ResultSet rs, int i) throws SQLException{
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

}
