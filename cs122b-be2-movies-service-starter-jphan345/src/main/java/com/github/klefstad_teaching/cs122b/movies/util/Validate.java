package com.github.klefstad_teaching.cs122b.movies.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

@Component
public class Validate
{
    public void checkMovieOrderBy(String orderBy) {
        if (!orderBy.equals("title") && !orderBy.equals("rating") && !orderBy.equals("year")) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public void checkPersonOrderBy(String orderBy) {
        if (!orderBy.equals("name") && !orderBy.equals("popularity") && !orderBy.equals("birthday")) {
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

    public boolean canSeeHidden(SignedJWT user) {
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
        return canSeeHidden;
    }
}
