package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.data.*;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieByIdResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchPersonIdRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieRepo
{
    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper               objectMapper;

    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    public List<Movie> searchMovie(MovieSearchRequest request, boolean canSeeHidden) {
        // build sql query
        StringBuilder         sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();
        boolean               whereAdded = false;

        //language=sql
        sql = new StringBuilder(
                "SELECT DISTINCT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                "FROM movies.movie m " +
                "    JOIN movies.person p ON m.director_id = p.id " +
                "    JOIN movies.movie_genre mg ON m.id = mg.movie_id " +
                "    JOIN movies.genre g ON mg.genre_id = g.id "
        );

        // if the user doesn't have permission to see hidden movies
        if (!canSeeHidden) {
            sql.append(" WHERE m.hidden = false ");
            whereAdded = true;
        }

        // add search for title
        if (request.getTitle().isPresent()) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" m.title LIKE :title ");

            String substringSearchTitle = '%' + request.getTitle().get() + '%';
            source.addValue("title", substringSearchTitle, Types.VARCHAR);
        }

        // add search for year
        if (request.getYear().isPresent()) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" m.year = :year ");
            source.addValue("year", request.getYear().get(), Types.INTEGER);
        }

        // add search for director
        if (request.getDirector().isPresent()) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" p.name LIKE :name ");

            String substringSearchDirector = '%' + request.getDirector().get() + '%';
            source.addValue("name", substringSearchDirector, Types.VARCHAR);
        }

        // add search for genre
        if (request.getGenre().isPresent()) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" g.name LIKE :genre ");

            String substringSearchGenre = '%' + request.getGenre().get() + '%';
            source.addValue("genre", substringSearchGenre, Types.VARCHAR);
        }

        // add order by sql
        String orderByString = null;
        if (request.getOrderBy().isPresent()) {
            orderByString = request.getOrderBy().get();
        }
        MovieOrderBy orderBy = MovieOrderBy.fromString(orderByString);
        sql.append(orderBy.toSql());

        // add DESC direction if necessary
        if (request.getDirection().isPresent() && request.getDirection().get().equalsIgnoreCase("desc")) {
            sql.append(" DESC ");
        }

        // add secondary order by (always movie id ASC)
        sql.append(" , m.id ");

        // add page limit
        if (request.getLimit().isPresent()) {
            sql.append(" LIMIT :limit ");
            source.addValue("limit", request.getLimit().get(), Types.INTEGER);
        }

        // add page offset
        if (request.getPage().isPresent()) {
            sql.append(" OFFSET :offset ");

            // calculate page offset
            int limit = request.getLimit().get();
            int page = request.getPage().get();
            int offset = (limit * page) - limit;

            source.addValue("offset", offset, Types.INTEGER);
        }

        // execute sql query
        sql.append(";");
        return this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) ->
                        new Movie()
                                .setId(rs.getLong("id"))
                                .setTitle(rs.getString("title"))
                                .setYear(rs.getInt("year"))
                                .setDirector(rs.getString("name"))
                                .setRating(rs.getDouble("rating"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
                                .setHidden(rs.getBoolean("hidden"))
        );
    }

    public List<Movie> searchMovieByPersonId(MovieSearchPersonIdRequest request, long personId, boolean canSeeHidden) {
        // build sql query
        StringBuilder         sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();
        boolean               whereAdded = false;

        //language=sql
        sql = new StringBuilder(
                "SELECT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                "FROM ( " +
                "     SELECT * " +
                "     FROM movies.movie m " +
                "              JOIN movies.movie_person mp ON mp.movie_id = m.id " +
                "     WHERE mp.person_id = :personId " +
                "     ) as m " +
                "        JOIN movies.person p ON m.director_id = p.id "
        );

        source.addValue("personId", personId, Types.INTEGER);

        // if the user doesn't have permission to see hidden movies
        if (!canSeeHidden) {
            sql.append(" WHERE m.hidden = false ");
            whereAdded = true;
        }

        // add order by sql
        String orderByString = null;
        if (request.getOrderBy().isPresent()) {
            orderByString = request.getOrderBy().get();
        }
        MovieOrderBy orderBy = MovieOrderBy.fromString(orderByString);
        sql.append(orderBy.toSql());

        // add DESC direction if necessary
        if (request.getDirection().isPresent() && request.getDirection().get().equalsIgnoreCase("desc")) {
            sql.append(" DESC ");
        }

        // add secondary order by (always movie id ASC)
        sql.append(" , m.id ");

        // add page limit
        if (request.getLimit().isPresent()) {
            sql.append(" LIMIT :limit ");
            source.addValue("limit", request.getLimit().get(), Types.INTEGER);
        }

        // add page offset
        if (request.getPage().isPresent()) {
            sql.append(" OFFSET :offset ");

            // calculate page offset
            int limit = request.getLimit().get();
            int page = request.getPage().get();
            int offset = (limit * page) - limit;

            source.addValue("offset", offset, Types.INTEGER);
        }

        // execute sql query
        sql.append(";");
        return this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) ->
                        new Movie()
                                .setId(rs.getLong("id"))
                                .setTitle(rs.getString("title"))
                                .setYear(rs.getInt("year"))
                                .setDirector(rs.getString("name"))
                                .setRating(rs.getDouble("rating"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
                                .setHidden(rs.getBoolean("hidden"))
        );
    }

    public MovieByIdResponse movieSearchById(long movieId, boolean canSeeHidden) {
        //language=sql
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT m.id, m.title, m.year, p.name, m.rating, m.num_votes, m.budget, m.revenue, " +
                "m.overview, m.backdrop_path, m.poster_path, m.hidden, " +
                "(SELECT JSON_ARRAYAGG(JSON_OBJECT('id', g.id, 'name', g.name)) " +
                "    FROM (SELECT DISTINCT g.id, g.name " +
                "         FROM movies.genre g " +
                "              JOIN movies.movie_genre mg ON mg.genre_id = g.id " +
                "              JOIN movies.movie m ON m.id = mg.movie_id " +
                "         WHERE m.id = :movieId " +
                "         ORDER BY g.name) as g) AS genres, " +
                "(SELECT JSON_ARRAYAGG(JSON_OBJECT('id', p.id, 'name', p.name)) " +
                "    FROM (SELECT DISTINCT p.id, p.name, p.popularity " +
                "          FROM movies.person p " +
                "               JOIN movies.movie_person mp ON mp.person_id = p.id " +
                "               JOIN movies.movie m ON m.id = mp.movie_id " +
                "          WHERE m.id = :movieId " +
                "          ORDER BY p.popularity DESC, p.id) as p) AS persons " +
                "FROM movies.movie m " +
                "    JOIN movies.person p ON m.director_id = p.id " +
                "    JOIN movies.movie_genre mg ON m.id = mg.movie_id " +
                "    JOIN movies.genre g ON mg.genre_id = g.id " +
                "WHERE m.id = :movieId"
        );

        // if the user is not an admin or employee, they can't see hidden movies
        if (!canSeeHidden) {
            sql.append(" AND m.hidden = false ");
        }
        sql.append(";");

        try {
            return this.template.queryForObject(
                    sql.toString(),
                    new MapSqlParameterSource().addValue("movieId", movieId, Types.INTEGER),
                    this::mapMovieId
            );
        } catch (DataAccessException e) {
            throw new ResultError(MoviesResults.NO_MOVIE_WITH_ID_FOUND);
        }
    }

    private MovieByIdResponse mapMovieId(ResultSet rs, int rowNum)
        throws SQLException {
        List<Genre> genres;
        List<Person> persons;

        try {
            String genreArrayString = rs.getString("genres");
            String personArrayString = rs.getString("persons");

            Genre[] genreArray =
                    objectMapper.readValue(genreArrayString, Genre[].class);
            Person[] personArray =
                    objectMapper.readValue(personArrayString, Person[].class);

            // This just helps convert from an Object Array to a List<>
            genres = Arrays.stream(genreArray).collect(Collectors.toList());
            persons = Arrays.stream(personArray).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting string array to List");
        }

        MovieDetails movie =
                new MovieDetails()
                        .setId(rs.getLong("id"))
                        .setTitle(rs.getString("title"))
                        .setYear(rs.getInt("year"))
                        .setDirector(rs.getString("name"))
                        .setRating(rs.getDouble("rating"))
                        .setNumVotes(rs.getLong("num_votes"))
                        .setBudget(rs.getLong("budget"))
                        .setRevenue(rs.getLong("revenue"))
                        .setOverview(rs.getString("overview"))
                        .setBackdropPath(rs.getString("backdrop_path"))
                        .setPosterPath(rs.getString("poster_path"))
                        .setHidden(rs.getBoolean("hidden"));

        return new MovieByIdResponse()
                .setMovie(movie)
                .setGenres(genres)
                .setPersons(persons);
    }

    public List<PersonDetails> searchPerson(PersonSearchRequest request, boolean canSeeHidden) {
        // build sql query
        StringBuilder         sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();
        boolean               whereAdded = false;

        //language=sql
        sql = new StringBuilder(
                "SELECT DISTINCT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path " +
                "    FROM movies.person p "
        );

        // add search for movie titles person is in
        if (request.getMovieTitle() != null && request.getMovieTitle().isPresent()) {
            sql.append(
                    "    JOIN movies.movie_person mp ON mp.person_id = p.id " +
                    "    JOIN movies.movie m ON m.id = mp.movie_id " +
                    "WHERE m.title LIKE :title ");
            whereAdded = true;

            String substringSearchMovieTitle = '%' + request.getMovieTitle().get() + '%';
            source.addValue("title", substringSearchMovieTitle, Types.VARCHAR);
        }

        // if the user doesn't have permission to see hidden movies
        if (!canSeeHidden) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" m.hidden = false ");
        }

        // add search for name
        if (request.getName() != null && request.getName().isPresent()) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" p.name LIKE :name ");

            String substringSearchName = '%' + request.getName().get() + '%';
            source.addValue("name", substringSearchName, Types.VARCHAR);
        }

        // add search for birthday
        if (request.getBirthday() != null && request.getBirthday().isPresent()) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" p.birthday = :birthday ");
            source.addValue("birthday", request.getBirthday().get(), Types.DATE);
        }

        // add order by sql
        String orderByString = null;
        if (request.getOrderBy().isPresent()) {
            orderByString = request.getOrderBy().get();
        }
        PersonOrderBy orderBy = PersonOrderBy.fromString(orderByString);
        sql.append(orderBy.toSql());

        // add DESC direction if necessary
        if (request.getDirection().isPresent() && request.getDirection().get().equalsIgnoreCase("desc")) {
            sql.append(" DESC ");
        }

        // add secondary order by (always movie id ASC)
        sql.append(" , p.id ");

        // add page limit
        if (request.getLimit().isPresent()) {
            sql.append(" LIMIT :limit ");
            source.addValue("limit", request.getLimit().get(), Types.INTEGER);
        }

        // add page offset
        if (request.getPage().isPresent()) {
            sql.append(" OFFSET :offset ");

            // calculate page offset
            int limit = request.getLimit().get();
            int page = request.getPage().get();
            int offset = (limit * page) - limit;

            source.addValue("offset", offset, Types.INTEGER);
        }

        // execute sql query
        sql.append(";");
        return this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) ->
                        new PersonDetails()
                                .setId(rs.getLong("id"))
                                .setName(rs.getString("name"))
                                .setBirthday(rs.getString("birthday"))
                                .setBiography(rs.getString("biography"))
                                .setBirthplace(rs.getString("birthplace"))
                                .setPopularity(rs.getFloat("popularity"))
                                .setProfilePath(rs.getString("profile_path"))
        );
    }

    public PersonDetails personSearchById(long personId) {
        PersonDetails person;
        try {
            person = this.template.queryForObject(
                    //language=sql
                    "SELECT DISTINCT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path " +
                            "    FROM movies.person p " +
                            "WHERE p.id = :personId;",

                    new MapSqlParameterSource().addValue("personId", personId, Types.INTEGER),

                    (ResultSet rs, int rowNum) ->
                        new PersonDetails()
                                .setId(rs.getLong("id"))
                                .setName(rs.getString("name"))
                                .setBirthday(rs.getString("birthday"))
                                .setBiography(rs.getString("biography"))
                                .setBirthplace(rs.getString("birthplace"))
                                .setPopularity(rs.getFloat("popularity"))
                                .setProfilePath(rs.getString("profile_path"))
            );
        } catch (DataAccessException e) {
            throw new ResultError(MoviesResults.NO_PERSON_WITH_ID_FOUND);
        }

        return person;
    }
}
