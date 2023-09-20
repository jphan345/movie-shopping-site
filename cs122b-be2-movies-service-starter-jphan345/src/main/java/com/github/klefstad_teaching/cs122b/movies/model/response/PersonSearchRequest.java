package com.github.klefstad_teaching.cs122b.movies.model.response;

import java.util.Optional;

public class PersonSearchRequest {
    private Optional<String> name;
    private Optional<String> birthday;
    private Optional<String> movieTitle;
    private Optional<Integer> limit;
    private Optional<Integer> page;
    private Optional<String> orderBy;
    private Optional<String> direction;

    public Optional<String> getName() {
        return name;
    }

    public PersonSearchRequest setName(Optional<String> name) {
        this.name = name;
        return this;
    }

    public Optional<String> getBirthday() {
        return birthday;
    }

    public PersonSearchRequest setBirthday(Optional<String> birthday) {
        this.birthday = birthday;
        return this;
    }

    public Optional<String> getMovieTitle() {
        return movieTitle;
    }

    public PersonSearchRequest setMovieTitle(Optional<String> movieTitle) {
        this.movieTitle = movieTitle;
        return this;
    }

    public Optional<Integer> getLimit() {
        return limit;
    }

    public PersonSearchRequest setLimit(Optional<Integer> limit) {
        this.limit = limit;
        return this;
    }

    public Optional<Integer> getPage() {
        return page;
    }

    public PersonSearchRequest setPage(Optional<Integer> page) {
        this.page = page;
        return this;
    }

    public Optional<String> getOrderBy() {
        return orderBy;
    }

    public PersonSearchRequest setOrderBy(Optional<String> orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public Optional<String> getDirection() {
        return direction;
    }

    public PersonSearchRequest setDirection(Optional<String> direction) {
        this.direction = direction;
        return this;
    }
}
