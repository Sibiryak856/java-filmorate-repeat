package ru.yandex.practicum.filmorate.storage.dbImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        review.setId(insert.executeAndReturnKey(toMap(review)).longValue());
        return review;
    }

    @Override
    public Optional<Review> findById(long id) {
        return jdbcTemplate.query(
                "SELECT r.* " +
                        "FROM reviews AS r " +
                        "WHERE review_id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id),
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(makeReview(rs, 1));
                }
        );
    }

    @Override
    public void update(Review review) {

    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update(
                "DELETE FROM reviews WHERE review_id = :id",
                new MapSqlParameterSource().addValue("id", id)
        );
    }

    @Override
    public List<Review> getAllByFilmId(long filmId, int count) {
        return jdbcTemplate.query(
                "SELECT r.* " +
                        "FROM reviews AS r " +
                        "WHERE (:filmId is null or film_id = :filmId) " +
                        "LIMIT :count",
                new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("count", count),
                this :: makeReview
        );
    }

    @Override
    public void updateLike(long id, long userId, boolean isPositive) {
        jdbcTemplate.update(
                "INSERT INTO reviews_likes (review_id, user_id, is_positive) " +
                        "VALUES (:id, :userId, :isPositive)",
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("userId", userId)
                        .addValue("is_positive", isPositive)
        );
    }

    @Override
    public void deleteLike(long id, long userId, boolean isPositive) {
        jdbcTemplate.update(
                "DELETE FROM reviews_likes " +
                        "WHERE review_id = :id " +
                        "AND user_id = :userId " +
                        "AND isPositive = :isPositive",
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("userId", userId)
                        .addValue("is_positive", isPositive)
        );
    }

    private Map<String, Object> toMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        return values;
    }

    private Review makeReview(ResultSet rs, int i) throws SQLException {
        return Review.builder()
                .id(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
    }
}
