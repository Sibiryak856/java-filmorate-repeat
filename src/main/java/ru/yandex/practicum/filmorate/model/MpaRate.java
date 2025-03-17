package ru.yandex.practicum.filmorate.model;

public enum MpaRate {
    G,
    PG,
    PG_13,
    R,
    NC_17;

    private int id;
    private String name;
    private String description;

    MpaRate() {
    }

    MpaRate(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
