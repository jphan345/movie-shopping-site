package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.data.MovieOrderBy;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;

@Component
public class MovieRepo
{
    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper               objectMapper;

    //language=sql
    private final String MOVIE_SEARCH =
            "SELECT DISTINCT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
            "FROM movies.movie m " +
            "    JOIN movies.person p ON m.director_id = p.id " +
            "    JOIN movies.movie_genre mg ON m.id = mg.movie_id " +
            "    JOIN movies.genre g ON mg.genre_id = g.id ";

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

        sql = new StringBuilder(MOVIE_SEARCH);

        // if the user doesn't have permission to see hidden movies
        if (!canSeeHidden) {
            sql.append(" WHERE m.hidden = false");
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
}
