package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.data.Movie;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartRetrieveResponse;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

@RestController
public class CartController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public CartController(BillingRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @PostMapping("/cart/insert")
    public ResponseEntity<CartResponse> insertCart(@AuthenticationPrincipal SignedJWT user,
                                                   @RequestBody CartRequest request) {
        // validate request input
        validate.validateQuantity(request.getQuantity());

        try {
            JWTClaimsSet claimsSet = user.getJWTClaimsSet();
            Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

            repo.cartInsert(userId, request.getMovieId(), request.getQuantity());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new CartResponse().setResult(BillingResults.CART_ITEM_INSERTED));
        } catch (ParseException e) {
            throw new ResultError(Result.NO_RESULT);
        }
    }

    @PostMapping("/cart/update")
    public ResponseEntity<CartResponse> updateCart(@AuthenticationPrincipal SignedJWT user,
                                                   @RequestBody CartRequest request) throws ParseException {
        // validate request input
        validate.validateQuantity(request.getQuantity());

        JWTClaimsSet claimsSet = user.getJWTClaimsSet();
        Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

        // query for the item, if the item doesn't exist throw an error
        repo.cartItemExists(userId, request.getMovieId());

        repo.cartUpdate(userId, request.getMovieId(), request.getQuantity());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CartResponse().setResult(BillingResults.CART_ITEM_UPDATED));
    }

    @DeleteMapping("/cart/delete/{movieId}")
    public ResponseEntity<CartResponse> deleteMovieInCart(@AuthenticationPrincipal SignedJWT user,
                                                          @PathVariable("movieId") long movieId) throws ParseException {
        JWTClaimsSet claimsSet = user.getJWTClaimsSet();
        Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

        // query for the item, if the item doesn't exist throw an error
        repo.cartItemExists(userId, movieId);

        repo.cartDelete(userId, movieId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CartResponse().setResult(BillingResults.CART_ITEM_DELETED));
    }

    public BigDecimal getCartTotal(List<Movie> movies, boolean isPremiumUser) {
        // apply discount to premium users
        if (isPremiumUser) {
            for (Movie movie : movies) {
                int discount = repo.getDiscount(movie.getMovieId()).getDiscount();
                BigDecimal discPercent = BigDecimal.valueOf(1 - (discount / 100.0));
                BigDecimal discountedUnitPrice = movie.getUnitPrice().multiply(discPercent);
                discountedUnitPrice = discountedUnitPrice.setScale(2, RoundingMode.DOWN);

                movie.setUnitPrice(discountedUnitPrice);
            }
        }

        // get the total
        BigDecimal total = BigDecimal.ZERO;
        for (Movie movie : movies) {
            BigDecimal unitPrice = movie.getUnitPrice();
            BigDecimal quantity = BigDecimal.valueOf(movie.getQuantity());

            total = total.add(unitPrice.multiply(quantity)).setScale(2, RoundingMode.DOWN);
        }

        return total;
    }

    @GetMapping("/cart/retrieve")
    public ResponseEntity<CartRetrieveResponse> retrieveCart(@AuthenticationPrincipal SignedJWT user) throws ParseException {
        JWTClaimsSet claimsSet = user.getJWTClaimsSet();
        Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

        boolean isPremiumUser = validate.isPremiumUser(claimsSet);

        // retrieve user's cart
        List<Movie> movies = repo.cartRetrieve(userId);

        // create the response object
        CartRetrieveResponse response = new CartRetrieveResponse();
        if (movies.size() == 0) {
            response.setResult(BillingResults.CART_EMPTY);
        }
        else {
            response.setResult(BillingResults.CART_RETRIEVED);
            response.setItems(movies);

            BigDecimal total = getCartTotal(movies, isPremiumUser);
            response.setTotal(total);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/cart/clear")
    public ResponseEntity<CartResponse> clearCart(@AuthenticationPrincipal SignedJWT user) throws ParseException {
        JWTClaimsSet claimsSet = user.getJWTClaimsSet();
        Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

        CartResponse response = new CartResponse();

        try {
            repo.cartClear(userId);
            response.setResult(BillingResults.CART_CLEARED);
        } catch (DataAccessException e) {
            response.setResult(BillingResults.CART_EMPTY);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CartResponse().setResult(BillingResults.CART_CLEARED));
    }
}
