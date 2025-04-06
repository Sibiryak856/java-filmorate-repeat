package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@RestController
@Slf4j
@Validated
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("Request received: POST /reviews: {}", review);
        Review created = reviewService.create(review);
        log.info("Request POST processed: {}", created);
        return created;
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Request received: PUT /reviews: {}", review);
        Review updated = reviewService.update(review);
        log.info("Request PUT processed: {}", updated);
        return updated;
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        log.info("Request received: GET /reviews/{}", id);
        Review review = reviewService.getById(id);
        log.info("Request GET processed: {}", review);
        return review;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("Request received: DELETE /reviews/{}", id);
        reviewService.deleteById(id);
        log.info("Request DELETE processed");
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(defaultValue = "0") Long filmId,
                                   @RequestParam(defaultValue = "10", required = false) @Positive Integer count) {
        log.info("Request received GET /reviews?filmId={}&count={}", filmId, count);
        List<Review> reviews = reviewService.getAllByFilmId(filmId, count);
        log.info("Request processed GET {}", reviews);
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        log.info("Request received PUT /{}/like/{}", id, userId);
        reviewService.updateLikes(id, userId, TRUE, RequestMethod.PUT);
        log.info("Request PUT processed");
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id,
                           @PathVariable Long userId) {
        log.info("Request received PUT /{}/dislike/{}", id, userId);
        reviewService.updateLikes(id, userId, FALSE, RequestMethod.PUT);
        log.info("Request PUT processed");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        log.info("Request received DELETE /{}/like/{}", id, userId);
        reviewService.updateLikes(id, userId, TRUE, RequestMethod.DELETE);
        log.info("Request DELETE processed");
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id,
                           @PathVariable Long userId) {
        log.info("Request received DELETE /{}/dislike/{}", id, userId);
        reviewService.updateLikes(id, userId, FALSE, RequestMethod.DELETE);
        log.info("Request DELETE processed");
    }


}
