package com.github.klefstad_teaching.cs122b.movies.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import org.springframework.stereotype.Component;

@Component
public class Validate
{
    public void checkOrderBy(String orderBy) {
        if (!orderBy.equals("title") && !orderBy.equals("rating") && !orderBy.equals("year")) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public void checkDirection(String direction) {
        if (!direction.equals("asc") && !direction.equals("desc")) {
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
    }

    public void checkLimit(Integer limit) {
        if (limit != 10 && limit != 25 && limit != 50 && limit != 100) {
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        }
    }

    public void checkPage(Integer page) {
        if (page < 1) {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }
    }
}
