package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Director {

    private Long id;
    @NotBlank
    private String name;
}
