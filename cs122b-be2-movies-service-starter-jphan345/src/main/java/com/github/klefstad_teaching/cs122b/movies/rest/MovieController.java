package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.data.PersonDetails;
import com.github.klefstad_teaching.cs122b.movies.model.response.*;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class MovieController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping("/movie/search")
    public ResponseEntity<MovieSearchResponse> movieSearch(@AuthenticationPrincipal SignedJWT user,
                                                           MovieSearchRequest request) {
        boolean canSeeHidden = validate.canSeeHidden(user);

        // set default values if values aren't provided and validate
        if (request.getLimit() == null || !request.getLimit().isPresent()) { request.setLimit(Optional.of(10)); }
        validate.checkLimit(request.getLimit().get());

        if (request.getPage() == null || !request.getPage().isPresent()) { request.setPage(Optional.of(1)); }
        validate.checkPage(request.getPage().get());

        if (request.getOrderBy() == null || !request.getOrderBy().isPresent()) { request.setOrderBy(Optional.of("title")); }
        validate.checkMovieOrderBy(request.getOrderBy().get());

        if (request.getDirection() == null || !request.getDirection().isPresent()) { request.setDirection(Optional.of("asc")); }
        validate.checkDirection(request.getDirection().get());

        System.out.println(request.getTitle());
        System.out.println(request.getYear());
        System.out.println(request.getDirector());
        System.out.println(request.getGenre());
        System.out.println(request.getOrderBy());
        System.out.println(request.getDirection());
        System.out.println(request.getLimit());
        System.out.println(request.getPage());


        // search for movie in database
        List<Movie> movies = repo.searchMovie(request, canSeeHidden);

        // build response object and return
        MovieSearchResponse body = new MovieSearchResponse();
        if (movies.size() == 0) {
            body.setMovies(null)
                .setResult(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH);
        }
        else {
            body.setMovies(movies)
                .setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/movie/search/person/{personId}")
    public ResponseEntity<MovieSearchResponse> movieSearchPersonId(@AuthenticationPrincipal SignedJWT user,
                                                                           @PathVariable("personId") long personId,
                                                                           MovieSearchPersonIdRequest request) {
        boolean canSeeHidden = validate.canSeeHidden(user);

        // set default values if values aren't provided and validate
        if (request.getLimit() == null || !request.getLimit().isPresent()) { request.setLimit(Optional.of(10)); }
        validate.checkLimit(request.getLimit().get());

        if (request.getPage() == null || !request.getPage().isPresent()) { request.setPage(Optional.of(1)); }
        validate.checkPage(request.getPage().get());

        if (request.getOrderBy() == null || !request.getOrderBy().isPresent()) { request.setOrderBy(Optional.of("title")); }
        validate.checkMovieOrderBy(request.getOrderBy().get());

        if (request.getDirection() == null || !request.getDirection().isPresent()) { request.setDirection(Optional.of("asc")); }
        validate.checkDirection(request.getDirection().get());

        // search for movie in database
        List<Movie> movies = repo.searchMovieByPersonId(request, personId, canSeeHidden);

        // build response object and return
        MovieSearchResponse body = new MovieSearchResponse();
        if (movies.size() == 0) {
            body.setMovies(null).setResult(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND);
        }
        else {
            body.setMovies(movies).setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<MovieByIdResponse> searchMovieById(@AuthenticationPrincipal SignedJWT user,
                                                       @PathVariable("movieId") long movieId) {
        boolean canSeeHidden = validate.canSeeHidden(user);

        MovieByIdResponse body = repo.movieSearchById(movieId, canSeeHidden)
                .setResult(MoviesResults.MOVIE_WITH_ID_FOUND);

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/person/search")
    public ResponseEntity<PersonSearchResponse> searchPerson(@AuthenticationPrincipal SignedJWT user,
                                                             PersonSearchRequest request) {
        boolean canSeeHidden = validate.canSeeHidden(user);

        // set default values if values aren't provided and validate
        if (request.getLimit() == null || !request.getLimit().isPresent()) { request.setLimit(Optional.of(10)); }
        validate.checkLimit(request.getLimit().get());

        if (request.getPage() == null || !request.getPage().isPresent()) { request.setPage(Optional.of(1)); }
        validate.checkPage(request.getPage().get());

        if (request.getOrderBy() == null || !request.getOrderBy().isPresent()) { request.setOrderBy(Optional.of("name")); }
        validate.checkPersonOrderBy(request.getOrderBy().get());

        if (request.getDirection() == null || !request.getDirection().isPresent()) { request.setDirection(Optional.of("asc")); }
        validate.checkDirection(request.getDirection().get());

        // search for movie in database
        List<PersonDetails> persons = repo.searchPerson(request, canSeeHidden);

        // build response object and return
        PersonSearchResponse body = new PersonSearchResponse();
        if (persons.size() == 0) {
            body.setPersons(null)
                    .setResult(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH);
        }
        else {
            body.setPersons(persons)
                    .setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<PersonByIdResponse> getPersonById(@AuthenticationPrincipal SignedJWT user,
                                                             @PathVariable("personId") long personId) {
        // make sure given SignedJWT is valid
        validate.canSeeHidden(user);

        PersonDetails person = repo.personSearchById(personId);

        PersonByIdResponse body = new PersonByIdResponse()
                .setPerson(person)
                .setResult(MoviesResults.PERSON_WITH_ID_FOUND);

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }
}
