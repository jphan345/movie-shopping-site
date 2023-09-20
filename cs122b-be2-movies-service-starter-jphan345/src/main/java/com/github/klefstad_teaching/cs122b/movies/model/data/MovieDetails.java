package com.github.klefstad_teaching.cs122b.movies.model.data;

public class MovieDetails {
    private long id;
    private String title;
    private int year;
    private String director;
    private double rating;
    private long numVotes;
    private long budget;
    private long revenue;
    private String overview;
    private String backdropPath;
    private String posterPath;
    private boolean hidden;

    public long getId() {
        return id;
    }

    public MovieDetails setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MovieDetails setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getYear() {
        return year;
    }

    public MovieDetails setYear(int year) {
        this.year = year;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public MovieDetails setDirector(String director) {
        this.director = director;
        return this;
    }

    public double getRating() {
        return rating;
    }

    public MovieDetails setRating(double rating) {
        this.rating = rating;
        return this;
    }

    public long getNumVotes() {
        return numVotes;
    }

    public MovieDetails setNumVotes(long numVotes) {
        this.numVotes = numVotes;
        return this;
    }

    public long getBudget() {
        return budget;
    }

    public MovieDetails setBudget(long budget) {
        this.budget = budget;
        return this;
    }

    public long getRevenue() {
        return revenue;
    }

    public MovieDetails setRevenue(long revenue) {
        this.revenue = revenue;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public MovieDetails setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public MovieDetails setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public MovieDetails setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public MovieDetails setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
