package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;

@Component
public class IDMAuthenticationManager
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String       HASH_FUNCTION = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS     = 10000;
    private static final int KEY_BIT_LENGTH = 512;

    private static final int SALT_BYTE_LENGTH = 4;

    public final IDMRepo repo;

    @Autowired
    public IDMAuthenticationManager(IDMRepo repo)
    {
        this.repo = repo;
    }

    private static byte[] hashPassword(final char[] password, String salt)
    {
        return hashPassword(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_FUNCTION);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BIT_LENGTH);

            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] genSalt()
    {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    public User selectUser(String email) {
        return repo.selectUser(email);
    }

    public User selectAndAuthenticateUser(String email, char[] password)
    {
        User user = selectUser(email);
        byte[] encodedPassword = hashPassword(password, user.getSalt());

        if (!Base64.getEncoder().encodeToString(encodedPassword).equals(user.getHashedPassword())) {
            throw new ResultError(IDMResults.INVALID_CREDENTIALS);
        }
        if (user.getUserStatus() == UserStatus.LOCKED) {
            throw new ResultError(IDMResults.USER_IS_LOCKED);
        }
        if (user.getUserStatus() == UserStatus.BANNED) {
            throw new ResultError(IDMResults.USER_IS_BANNED);
        }

        return user;
    }

    public void createAndInsertUser(String email, char[] password)
    {
        // Salt and hash password
        byte[] salt = genSalt();
        byte[] encodedPassword = hashPassword(password, salt);

        String based64EncodedHashedPassword = Base64.getEncoder().encodeToString(encodedPassword);
        String based64EncodedHashedSalt = Base64.getEncoder().encodeToString(salt);

        User my_user = new User()
                .setEmail(email)
                .setUserStatus(UserStatus.ACTIVE)
                .setSalt(based64EncodedHashedSalt)
                .setHashedPassword(based64EncodedHashedPassword);

        // Store user in idm.user table
        repo.insertUser(my_user);
    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
        repo.insertRefreshToken(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token)
    {
        // validate token length and format
        if (token.length() != 36) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_LENGTH);
        }
        try {
            UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_FORMAT);
        }

        // get refresh token from database
        RefreshToken refreshToken = repo.getRefreshToken(token);

        return refreshToken;
    }

    public void updateRefreshTokenExpireTime(RefreshToken token)
    {
        repo.updateRefreshTokenExpireTime(token);
    }

    public void expireRefreshToken(RefreshToken token)
    {
        repo.updateRefreshTokenStatus(token, TokenStatus.EXPIRED);
    }

    public void revokeRefreshToken(RefreshToken token)
    {
        repo.updateRefreshTokenStatus(token, TokenStatus.REVOKED);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken)
    {
        int userId = refreshToken.getUserId();

        return repo.selectUser(userId);
    }
}
