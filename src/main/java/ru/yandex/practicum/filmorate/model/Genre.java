package ru.yandex.practicum.filmorate.model;

public enum Genre  {
    COMEDY("Комедия", 1),
    DRAMA("Драма", 2),
    CARTOON("Мультфильм", 3),
    THRILLER("Триллер", 4),
    DOCUMENTARY("Документальный", 5),
    ACTION("Боевик", 6);

    private String name;
    private int id;

    Genre(String name, int id) {
        this.name = name;
        this.id = id;
    }
}

