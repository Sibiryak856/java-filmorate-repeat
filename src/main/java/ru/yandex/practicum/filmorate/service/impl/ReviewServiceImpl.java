package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review create(Review review) {
        checkFieldsIsPresent(review.getUserId(), review.getFilmId());
        review.setUseful(0L);
        return reviewStorage.create(review);
    }

    @Override
    public Review update(Review review) {
        Review updating  = reviewStorage.findById(review.getReviewId())
                        .orElseThrow(() -> new NotFoundException("Review not found"));
        checkFieldsIsPresent(review.getUserId(), review.getFilmId());
        reviewStorage.update(review);
        return reviewStorage.findById(review.getReviewId()).get();
    }

    @Override
    public Review getById(long id) {
        return reviewStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));
    }

    @Override
    public void deleteById(long id) {
        reviewStorage.deleteById(id);
    }

    @Override
    public List<Review> getAllByFilmId(long filmId, int count) {
        return reviewStorage.getAllByFilmId(filmId, count);
    }

    @Override
    public void updateLikes(long id, long userId, boolean isPositive, RequestMethod method) {
        reviewStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        long useful = isPositive ? 1L : -1L;
        switch (method) {
            case PUT:
                reviewStorage.updateLike(id, userId, useful);
                break;
            case DELETE:
                reviewStorage.deleteLike(id, userId, useful);
                break;
            default:
                throw new NotFoundException("Unsupported request method");
        }

    }

    private void setUseful(Review review) {
        review.setUseful(reviewStorage.getUseFul(review.getReviewId()));
    }

    private void checkFieldsIsPresent(long userId, long filmId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found"));
    }
}
