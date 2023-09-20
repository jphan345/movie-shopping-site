package com.github.klefstad_teaching.cs122b.idm.repo;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;

@Component
public class IDMRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public User selectUser(String email) {
        try {
            User user = this.template.queryForObject(
                    "SELECT id, email, user_status_id, salt, hashed_password " +
                            "FROM idm.user " +
                            "WHERE email = :email;",

                    new MapSqlParameterSource()
                            .addValue("email", email, Types.VARCHAR),

                    (rs, rowNum) ->
                            new User()
                                    .setId(rs.getInt("id"))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt(rs.getString("salt"))
                                    .setHashedPassword(rs.getString("hashed_password"))
            );

            return user;
        } catch (DataAccessException e) {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }
    }

    public User selectUser(int userId) {
        try {
            User user = this.template.queryForObject(
                    "SELECT id, email, user_status_id, salt, hashed_password " +
                            "FROM idm.user " +
                            "WHERE id = :userId;",

                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER),

                    (rs, rowNum) ->
                            new User()
                                    .setId(rs.getInt("id"))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt(rs.getString("salt"))
                                    .setHashedPassword(rs.getString("hashed_password"))
            );

            return user;
        } catch (DataAccessException e) {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }
    }

    public int insertUser(User user) {
        int rowsUpdated = this.template.update(
                "INSERT INTO idm.user (id, email, user_status_id, salt, hashed_password)" +
                        "VALUES (:id, :email, :user_status_id, :salt, :hashed_password);",
                new MapSqlParameterSource()
                        .addValue("id", user.getId(), Types.INTEGER)
                        .addValue("email", user.getEmail(), Types.VARCHAR)
                        .addValue("user_status_id", user.getUserStatus().id(), Types.INTEGER)
                        .addValue("salt", user.getSalt(), Types.CHAR)
                        .addValue("hashed_password", user.getHashedPassword(), Types.CHAR)
        );

        return rowsUpdated;
    }

    public int insertRefreshToken(RefreshToken refreshToken) {
        int rowsUpdated = this.template.update(
                "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time, max_life_time)" +
                        "VALUES (:token, :user_id, :token_status_id, :expire_time, :max_life_time);",
                new MapSqlParameterSource()
                        .addValue("token", refreshToken.getToken(), Types.CHAR)
                        .addValue("user_id", refreshToken.getUserId(), Types.INTEGER)
                        .addValue("token_status_id", refreshToken.getTokenStatus().id(), Types.INTEGER)
                        .addValue("expire_time",
                                Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP)
                        .addValue("max_life_time",
                                Timestamp.from(refreshToken.getMaxLifeTime()), Types.TIMESTAMP)
        );

        return rowsUpdated;
    }

    public RefreshToken getRefreshToken(String token) {
        try {
            RefreshToken refreshToken = this.template.queryForObject(
                    "SELECT id, token, user_id, token_status_id, expire_time, max_life_time " +
                            "FROM idm.refresh_token " +
                            "WHERE token = :token;",

                    new MapSqlParameterSource()
                            .addValue("token", token, Types.CHAR),

                    (rs, rowNum) ->
                            new RefreshToken()
                                    .setId(rs.getInt("id"))
                                    .setToken(rs.getString("token"))
                                    .setUserId(rs.getInt("user_id"))
                                    .setTokenStatus(TokenStatus.fromId(rs.getInt("token_status_id")))
                                    .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                                    .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())
            );

            return refreshToken;
        } catch (DataAccessException e) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public int updateRefreshTokenExpireTime(RefreshToken token) {
        int rowsUpdated = this.template.update(
                "UPDATE idm.refresh_token " +
                        "SET expire_time = :expire_time " +
                        "WHERE id = :id;",

                new MapSqlParameterSource()
                        .addValue("expire_time", Timestamp.from(token.getExpireTime()))
                        .addValue("id", token.getId())
        );

        return rowsUpdated;
    }

    public int updateRefreshTokenStatus(RefreshToken token, TokenStatus tokenStatus) {
        int rowsUpdated = this.template.update(
                "UPDATE idm.refresh_token " +
                        "SET token_status_id = :token_status_id " +
                        "WHERE id = :id;",

                new MapSqlParameterSource()
                        .addValue("token_status_id", token.getTokenStatus().id())
                        .addValue("id", token.getId())
        );

        return rowsUpdated;
    }
}
