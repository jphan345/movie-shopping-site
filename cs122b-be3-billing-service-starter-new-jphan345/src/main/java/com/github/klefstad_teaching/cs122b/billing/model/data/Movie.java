package com.github.klefstad_teaching.cs122b.billing.model.data;

import java.math.BigDecimal;

public class Movie {
    private BigDecimal unitPrice;
    private int quantity;
    private long movieId;
    private String movieTitle;
    private String backdropPath;
    private String posterPath;

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Movie setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public Movie setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public long getMovieId() {
        return movieId;
    }

    public Movie setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public Movie setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
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
}
