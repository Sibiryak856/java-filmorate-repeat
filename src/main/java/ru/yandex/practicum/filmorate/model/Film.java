package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @MinimumDate
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Set<Long> likes;

    public void addLike(long userId) {
        likes.add(userId);
    }

    public void deleteLike(long userId) {
        likes.remove(userId);
    }

}
