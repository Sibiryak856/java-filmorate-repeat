package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review create(Review review);

    Review update(Review review);

    Review getById(long id);

    void deleteById(long id);

    List<Review> getAllByFilmId(long filmId, int count);

    void updateLikes(long id, long userId, boolean isPositive, RequestMethod method);
}
