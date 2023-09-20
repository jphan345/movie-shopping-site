package com.github.klefstad_teaching.cs122b.movies.model.response;

import java.util.Optional;

public class MovieSearchPersonIdRequest {
    private Optional<Integer> limit;
    private Optional<Integer> page;
    private Optional<String> orderBy;
    private Optional<String> direction;

    public Optional<Integer> getLimit() {
        return limit;
    }

    public MovieSearchPersonIdRequest setLimit(Optional<Integer> limit) {
        this.limit = limit;
        return this;
    }

    public Optional<Integer> getPage() {
        return page;
    }

    public MovieSearchPersonIdRequest setPage(Optional<Integer> page) {
        this.page = page;
        return this;
    }

    public Optional<String> getOrderBy() {
        return orderBy;
    }

    public MovieSearchPersonIdRequest setOrderBy(Optional<String> orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public Optional<String> getDirection() {
        return direction;
    }

    public MovieSearchPersonIdRequest setDirection(Optional<String> direction) {
        this.direction = direction;
        return this;
    }
}
