package com.github.klefstad_teaching.cs122b.movies.model.data;

public class Genre {
    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public Genre setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Genre setName(String name) {
        this.name = name;
        return this;
    }
}
