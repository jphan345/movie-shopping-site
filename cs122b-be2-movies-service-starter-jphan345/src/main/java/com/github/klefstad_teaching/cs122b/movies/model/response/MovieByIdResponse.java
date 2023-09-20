package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.model.data.Genre;
import com.github.klefstad_teaching.cs122b.movies.model.data.MovieDetails;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;

import java.util.List;

public class MovieByIdResponse extends ResponseModel<MovieByIdResponse> {
    private MovieDetails movie;
    private List<Genre> genres;
    private List<Person> persons;

    public MovieDetails getMovie() {
        return movie;
    }

    public MovieByIdResponse setMovie(MovieDetails movie) {
        this.movie = movie;
        return this;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public MovieByIdResponse setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public MovieByIdResponse setPersons(List<Person> persons) {
        this.persons = persons;
        return this;
    }
}
