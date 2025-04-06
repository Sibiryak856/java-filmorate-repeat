package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Optional<Review> findById(long id);

    void update(Review review);

    void deleteById(long id);

    List<Review> getAllByFilmId(Long filmId, int count);

    void updateLike(long id, long userId, long useful);

    void deleteLike(long id, long userId, long useful);

    Long getUseFul(long reviewId);
}
