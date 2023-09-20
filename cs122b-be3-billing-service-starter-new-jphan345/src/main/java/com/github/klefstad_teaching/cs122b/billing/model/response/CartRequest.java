package com.github.klefstad_teaching.cs122b.billing.model.response;

public class CartRequest {
    private long movieId;
    private int quantity;

    public long getMovieId() {
        return movieId;
    }

    public CartRequest setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public CartRequest setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }
}
