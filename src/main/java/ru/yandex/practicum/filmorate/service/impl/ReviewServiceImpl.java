package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    @Override
    public Review create(Review review) {
        return null;
    }

    @Override
    public Review update(Review review) {
        return null;
    }

    @Override
    public Review getById(long id) {
        return null;
    }

    @Override
    public void deleteById(long id) {

    }

    @Override
    public List<Review> getAllByFilmId(long filmId, int count) {
        return List.of();
    }

    @Override
    public void updateLikes(long id, long userId, boolean isPositive, RequestMethod method) {

    }
}
