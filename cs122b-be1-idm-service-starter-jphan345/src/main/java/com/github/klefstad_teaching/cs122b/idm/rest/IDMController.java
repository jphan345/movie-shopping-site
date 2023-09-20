package com.github.klefstad_teaching.cs122b.idm.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.model.response.*;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;

    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
    }

    private void validatePassword(char[] password) {
        /*
        Password must be between [10-20] alphanumeric characters (inclusive), contain at least one uppercase alpha,
        one lowercase alpha, and one numeric. Throws appropriate error if password is invalid.
         */

        // password requirements
        boolean upperCaseFound = false;
        boolean lowerCaseFound = false;
        boolean numericFound = false;

        if (password.length < 10 || password.length > 20) {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);
        }
        for (char c : password) {
            if (!Character.isLetterOrDigit(c)) {
                throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
            }

            if (Character.isUpperCase(c)) {
                upperCaseFound = true;
            }
            if (Character.isLowerCase(c)) {
                lowerCaseFound = true;
            }
            if (Character.isDigit(c)) {
                numericFound = true;
            }
        }
        if (!upperCaseFound || !lowerCaseFound || !numericFound) {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
        }
    }

    private void validateEmail(String email) {
        /*
        Email must be of the form [email]@[domain].[extension], be between [6-32] characters (inclusive),
        and contain only alphanumeric characters. Throws appropriate error if email is invalid.
         */

        // regex to match with email
        Pattern pattern = Pattern.compile("^([\\w]|[\\d])+@([\\w]|[\\d])+\\.([\\w]|[\\d])+$");
        Matcher matcher = pattern.matcher(email);

        if (!matcher.find()) {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);
        }
        if (email.length() < 6 || email.length() > 32) {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        char[] pw = request.getPassword();
        String email = request.getEmail();

        // validate input
        validatePassword(pw);
        validateEmail(email);

        // if we can select a user with the email without an error, user exists already
        try {
            authManager.selectUser(email);
        } catch (ResultError e) { // If we get an error that a user isn't found, we can proceed with registering
            authManager.createAndInsertUser(email, pw);

            RegisterResponse body = new RegisterResponse()
                    .setResult(IDMResults.USER_REGISTERED_SUCCESSFULLY);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(body);
        }

        throw new ResultError(IDMResults.USER_ALREADY_EXISTS);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody RegisterRequest request) {
        char[] password = request.getPassword();
        String email = request.getEmail();

        // validate input
        validatePassword(password);
        validateEmail(email);

        // authenticate that the email exists and the password is correct
        User myUser = authManager.selectAndAuthenticateUser(email, password);

        // create access token and refresh token
        String accessToken = jwtManager.buildAccessToken(myUser);
        RefreshToken refreshToken = jwtManager.buildRefreshToken(myUser);
        authManager.insertRefreshToken(refreshToken);

        LoginResponse body = new LoginResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken.getToken())
                .setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY);
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest request) {
        RefreshToken refreshToken = authManager.verifyRefreshToken(request.getRefreshToken());

        // check that refresh token status is not expired or revoked
        if (jwtManager.hasExpired(refreshToken)) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }
        if (refreshToken.getTokenStatus() == TokenStatus.REVOKED) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
        }

        // check if the token has expired
        Instant currentTime = Instant.now();
        if (currentTime.isAfter(refreshToken.getExpireTime()) || currentTime.isAfter(refreshToken.getMaxLifeTime())) {
            refreshToken.setTokenStatus(TokenStatus.EXPIRED);
            authManager.expireRefreshToken(refreshToken);

            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }

        // update token expire time
        jwtManager.updateRefreshTokenExpireTime(refreshToken);
        authManager.updateRefreshTokenExpireTime(refreshToken);

        // if expire time is after the max expire time, create a new refresh token and revoke old token
        if (jwtManager.needsRefresh(refreshToken)) {
            // update refresh token to revoked in db
            User user = authManager.getUserFromRefreshToken(refreshToken);
            refreshToken.setTokenStatus(TokenStatus.REVOKED);
            authManager.revokeRefreshToken(refreshToken);

            // build new refresh token and access token
            RefreshToken newRefreshToken = jwtManager.buildRefreshToken(user);
            authManager.insertRefreshToken(newRefreshToken);
            String accessToken = jwtManager.buildAccessToken(user);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new RefreshResponse()
                            .setRefreshToken(newRefreshToken.getToken())
                            .setAccessToken(accessToken)
                            .setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN));
        }
        else { // if expire time is before the max expire time, return same refresh token and new access token
            //update refresh token expire time in db
            authManager.updateRefreshTokenExpireTime(refreshToken);
            User user = authManager.getUserFromRefreshToken(refreshToken);
            // build new access token
            String accessToken = jwtManager.buildAccessToken(user);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new RefreshResponse()
                            .setRefreshToken(refreshToken.getToken())
                            .setAccessToken(accessToken)
                            .setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN));
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest request) {
        jwtManager.verifyAccessToken(request.getAccessToken());

        AuthenticateResponse body = new AuthenticateResponse()
                .setResult(IDMResults.ACCESS_TOKEN_IS_VALID);
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }
}
