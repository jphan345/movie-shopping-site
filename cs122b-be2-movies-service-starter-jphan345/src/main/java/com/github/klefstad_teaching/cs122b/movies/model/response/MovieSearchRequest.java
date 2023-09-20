package com.github.klefstad_teaching.cs122b.movies.model.response;

import java.util.Optional;

public class MovieSearchRequest {
    private Optional<String> title;
    private Optional<Integer> year;
    private Optional<String> director;
    private Optional<String> genre;
    private Optional<Integer> limit;
    private Optional<Integer> page;
    private Optional<String> orderBy;
    private Optional<String> direction;

    public MovieSearchRequest(Optional<String> title, Optional<Integer> year, Optional<String> director,
                              Optional<String> genre, Optional<Integer> limit, Optional<Integer> page,
                              Optional<String> orderBy, Optional<String> direction) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.limit = limit;
        this.page = page;
        this.orderBy = orderBy;
        this.direction = direction;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public MovieSearchRequest setTitle(Optional<String> title) {
        this.title = title;
        return this;
    }

    public Optional<Integer> getYear() {
        return year;
    }

    public MovieSearchRequest setYear(Optional<Integer> year) {
        this.year = year;
        return this;
    }

    public Optional<String> getDirector() {
        return director;
    }

    public MovieSearchRequest setDirector(Optional<String> director) {
        this.director = director;
        return this;
    }

    public Optional<String> getGenre() {
        return genre;
    }

    public MovieSearchRequest setGenre(Optional<String> genre) {
        this.genre = genre;
        return this;
    }

    public Optional<Integer> getLimit() {
        return limit;
    }

    public MovieSearchRequest setLimit(Optional<Integer> limit) {
        this.limit = limit;
        return this;
    }

    public Optional<Integer> getPage() {
        return page;
    }

    public MovieSearchRequest setPage(Optional<Integer> page) {
        this.page = page;
        return this;
    }

    public Optional<String> getOrderBy() {
        return orderBy;
    }

    public MovieSearchRequest setOrderBy(Optional<String> orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public Optional<String> getDirection() {
        return direction;
    }

    public MovieSearchRequest setDirection(Optional<String> direction) {
        this.direction = direction;
        return this;
    }
}
