package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class IDMJwtManager
{
    private final JWTManager jwtManager;

    @Autowired
    public IDMJwtManager(IDMServiceConfig serviceConfig)
    {
        this.jwtManager =
            new JWTManager.Builder()
                .keyFileName(serviceConfig.keyFileName())
                .accessTokenExpire(serviceConfig.accessTokenExpire())
                .maxRefreshTokenLifeTime(serviceConfig.maxRefreshTokenLifeTime())
                .refreshTokenExpire(serviceConfig.refreshTokenExpire())
                .build();
    }

    private SignedJWT buildAndSignJWT(JWTClaimsSet claimsSet)
        throws JOSEException
    {
        // build JWSHeader
        JWSHeader header = new JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
                .keyID(jwtManager.getEcKey().getKeyID())
                .type(JWTManager.JWS_TYPE)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(jwtManager.getSigner());

        return signedJWT;
    }

    private void verifyJWT(SignedJWT jwt)
        throws JOSEException, BadJOSEException
    {
        // verify that token is valid and issued by us
        jwt.verify(jwtManager.getVerifier());
        // verify that claims are consistent with what we expect
        jwtManager.getJwtProcessor().process(jwt, null);
    }

    public String buildAccessToken(User user)
    {
        // build JWTClaimsSet
        Instant currentTime = Instant.now();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .expirationTime(Date.from(currentTime.plus(jwtManager.getAccessTokenExpire())))
                .issueTime(Date.from(currentTime))
                .claim(JWTManager.CLAIM_ROLES, user.getRoles())
                .claim(JWTManager.CLAIM_ID, user.getId())
                .build();

        try {
            SignedJWT signedJWT = buildAndSignJWT(claimsSet);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
        }
    }

    public void verifyAccessToken(String jws)
    {
        try {
            // Rebuild the SignedJWT from the serialized String
            SignedJWT jwt = SignedJWT.parse(jws);

            // verify jwt
            verifyJWT(jwt);

            // Do logic to check if expired manually
            if (Instant.now().isAfter(jwt.getJWTClaimsSet().getExpirationTime().toInstant())) {
                throw new ResultError(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
            }

        } catch (IllegalStateException | JOSEException | BadJOSEException | ParseException e) {
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
        }
    }

    public RefreshToken buildRefreshToken(User user)
    {
        RefreshToken refreshToken = new RefreshToken()
                .setToken(generateUUID().toString())
                .setTokenStatus(TokenStatus.ACTIVE)
                .setUserId(user.getId())
                .setExpireTime(Instant.now().plus(jwtManager.getRefreshTokenExpire()))
                .setMaxLifeTime(Instant.now().plus(jwtManager.getMaxRefreshTokenLifeTime()));

        return refreshToken;
    }

    public boolean hasExpired(RefreshToken refreshToken)
    {
        return refreshToken.getTokenStatus() == TokenStatus.EXPIRED;
    }

    public boolean needsRefresh(RefreshToken refreshToken)
    {
        return refreshToken.getExpireTime().isAfter(refreshToken.getMaxLifeTime());
    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken)
    {
        refreshToken.setExpireTime(Instant.now().plus(jwtManager.getRefreshTokenExpire()));
    }

    private UUID generateUUID()
    {
        return UUID.randomUUID();
    }
}
