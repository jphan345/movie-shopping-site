package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTAuthenticationFilter;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
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
        boolean canSeeHidden = false;
        try {
            List<String> roles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

            for (String role : roles) {
                if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("employee")) {
                    canSeeHidden = true;
                    break;
                }
            }
        } catch (IllegalStateException | ParseException e) {
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
        }

        // set default values if values aren't provided and validate
        if (!request.getLimit().isPresent()) {
            request.setLimit(Optional.of(10));
        }
        validate.checkLimit(request.getLimit().get());

        if (!request.getPage().isPresent()) {
            request.setPage(Optional.of(1));
        }
        validate.checkPage(request.getPage().get());

        if (!request.getOrderBy().isPresent()) {
            request.setOrderBy(Optional.of("title"));
        }
        validate.checkOrderBy(request.getOrderBy().get());

        if (!request.getDirection().isPresent()) {
            request.setDirection(Optional.of("asc"));
        }
        validate.checkDirection(request.getDirection().get());

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
}
