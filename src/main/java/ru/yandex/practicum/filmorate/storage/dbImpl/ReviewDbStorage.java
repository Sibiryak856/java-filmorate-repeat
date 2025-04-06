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
        review.setReviewId(insert.executeAndReturnKey(toMap(review)).longValue());
        return review;
    }

    @Override
    public Optional<Review> findById(long id) {
        return jdbcTemplate.query(
                "SELECT r.*, COALESCE(SUM(rl.is_useful), 0) AS useful " +
                        "FROM reviews AS r " +
                        "LEFT OUTER JOIN reviews_likes AS rl ON r.review_id = rl.review_id " +
                        "WHERE r.review_id = :id " +
                        "GROUP BY r.review_id",
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
        jdbcTemplate.update(
                "UPDATE reviews SET " +
                        "content = :content, " +
                        "is_positive = :is_positive," +
                        "user_id = :user_id, " +
                        "film_id = :film_id " +
                        "WHERE review_id = :id",
                new MapSqlParameterSource(toMap(review))
                        .addValue("id", review.getReviewId())
        );
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
                "SELECT r.*, COALESCE(SUM(rl.is_useful), 0) AS useful " +
                        "FROM reviews AS r " +
                        "LEFT OUTER JOIN reviews_likes AS rl ON r.review_id = rl.review_id " +
                        "WHERE (:filmId is null or film_id = :filmId) " +
                        "GROUP BY r.review_id " +
                        "ORDER BY useful DESC " +
                        "LIMIT :count",
                new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("count", count),
                this :: makeReview
        );
    }

    @Override
    public void updateLike(long id, long userId, long useful) {
        jdbcTemplate.update(
                "MERGE INTO reviews_likes AS rl " +
                "USING (VALUES (:id, :userId, :useful)) AS vals (review_id, user_id, is_useful) " +
                "ON rl.review_id = vals.review_id AND rl.user_id = vals.user_id " +
                "WHEN MATCHED THEN " +
                "UPDATE SET is_useful = vals.is_useful " +
                "WHEN NOT MATCHED THEN " +
                "INSERT (review_id, user_id, is_useful) " +
                "VALUES (vals.review_id, vals.user_id, vals.is_useful)",
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("userId", userId)
                        .addValue("useful", useful)
        );
    }

    @Override
    public void deleteLike(long id, long userId, long useful) {
        jdbcTemplate.update(
                "DELETE FROM reviews_likes " +
                        "WHERE review_id = :id " +
                        "AND user_id = :userId " +
                        "AND is_useful = :useful",
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("userId", userId)
                        .addValue("useful", useful)
        );
    }

    @Override
    public Long getUseFul(long reviewId) {
        return jdbcTemplate.query(
                "SELECT COALESCE(SUM(is_useful), 0) " +
                        "FROM reviews_likes " +
                        "WHERE review_id = :reviewId " +
                        "GROUP BY review_id",
                new MapSqlParameterSource()
                        .addValue("reviewId", reviewId),
                rs -> {
                    return rs.getLong(1);
                }
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
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getLong("useful"))
                .build();
    }
}
