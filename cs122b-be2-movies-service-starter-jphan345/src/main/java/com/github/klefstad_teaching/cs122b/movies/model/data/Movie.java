package com.github.klefstad_teaching.cs122b.movies.model.data;

public class Movie {
    private long id;
    private String title;
    private int year;
    private String director;
    private double rating;
    private String backdropPath;
    private String posterPath;
    private boolean hidden;

    public long getId() {
        return id;
    }

    public Movie setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Movie setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getYear() {
        return year;
    }

    public Movie setYear(int year) {
        this.year = year;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public Movie setDirector(String director) {
        this.director = director;
        return this;
    }

    public double getRating() {
        return rating;
    }

    public Movie setRating(double rating) {
        this.rating = rating;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Movie setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Movie setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Movie setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
