package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.data.Movie;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.billing.model.response.*;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.Order;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
public class OrderController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public OrderController(BillingRepo repo,Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    public String getTitles(List<Movie> movies) {
        StringBuilder str = new StringBuilder();
        for (Movie movie : movies) {
            str.append(movie.getMovieTitle());
        }
        return str.toString();
    }

    @GetMapping("/order/payment")
    public ResponseEntity<OrderPaymentResponse> orderPayment(@AuthenticationPrincipal SignedJWT user)
            throws ParseException {
        JWTClaimsSet claimsSet = user.getJWTClaimsSet();
        Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

        boolean isPremiumUser = validate.isPremiumUser(claimsSet);

        // retrieve user's cart
        List<Movie> movies = repo.cartRetrieve(userId);
        if (movies.size() == 0) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new OrderPaymentResponse().setResult(BillingResults.CART_EMPTY));
        }

        // create the response object
        CartController cart = new CartController(this.repo, this.validate);

        Long amountInTotalCents = cart.getCartTotal(movies, isPremiumUser).movePointRight(2).longValueExact();
        String description = getTitles(movies);
        String strUserId = Long.toString(userId);

        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams
                        .builder()
                        .setCurrency("USD") // This will always be the same for our project
                        .setDescription(description)
                        .setAmount(amountInTotalCents)
                        // We use MetaData to keep track of the user that should pay for the order
                        .putMetadata("userId", strUserId)
                        .setAutomaticPaymentMethods(
                                // This will tell stripe to generate the payment methods automatically
                                // This will always be the same for our project
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        // create the payment intent
        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.create(paymentIntentCreateParams);
        } catch (StripeException e) {
            throw new ResultError(BillingResults.STRIPE_ERROR);
        }

        OrderPaymentResponse response = new OrderPaymentResponse()
                .setPaymentIntentId(paymentIntent.getId())
                .setClientSecret(paymentIntent.getClientSecret())
                .setResult(BillingResults.ORDER_PAYMENT_INTENT_CREATED);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/order/complete")
    public ResponseEntity<OrderCompleteResponse> orderComplete(@AuthenticationPrincipal SignedJWT user,
                                                               @RequestBody OrderCompleteRequest request)
            throws ParseException {
        JWTClaimsSet claimsSet = user.getJWTClaimsSet();
        Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

        boolean isPremiumUser = validate.isPremiumUser(claimsSet);

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());
        } catch (StripeException e) {
            throw new ResultError(BillingResults.STRIPE_ERROR);
        }

        // validate the payment intent
        if (!paymentIntent.getStatus().equalsIgnoreCase("succeeded")) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }
        if (!paymentIntent.getMetadata().get("userId").equalsIgnoreCase(Long.toString(userId))) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_WRONG_USER);
        }

        // create a new billing.sale record
        List<Movie> movies = repo.cartRetrieve(userId);
        CartController cart = new CartController(this.repo, this.validate);
        BigDecimal total = cart.getCartTotal(movies, isPremiumUser);

        Sale sale = new Sale()
                .setOrderDate(Instant.now())
                .setTotal(total);

        sale = repo.saleInsert(userId, sale);

        // populate the billing.sale_item with the contents of the user's billing.cart
        for (Movie movie : movies) {
            repo.saleItemInsert(sale.getSaleId(), movie.getMovieId(), movie.getQuantity());
        }

        // clear the current users cart
        repo.cartClear(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new OrderCompleteResponse().setResult(BillingResults.ORDER_COMPLETED));
    }

    @GetMapping("/order/list")
    public ResponseEntity<OrderListResponse> orderList(@AuthenticationPrincipal SignedJWT user) throws ParseException {
        JWTClaimsSet claimsSet = user.getJWTClaimsSet();
        Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

        List<Sale> sales = repo.saleList(userId);

        if (sales.size() == 0) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new OrderListResponse()
                            .setResult(BillingResults.ORDER_LIST_NO_SALES_FOUND));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new OrderListResponse()
                        .setSales(sales)
                        .setResult(BillingResults.ORDER_LIST_FOUND_SALES));
    }

    @GetMapping("/order/detail/{saleId}")
    public ResponseEntity<OrderDetailResponse> orderDetail(@AuthenticationPrincipal SignedJWT user,
                                                           @PathVariable("saleId") long saleId) throws ParseException {
        JWTClaimsSet claimsSet = user.getJWTClaimsSet();
        Long userId = claimsSet.getLongClaim(JWTManager.CLAIM_ID);

        boolean isPremiumUser = validate.isPremiumUser(claimsSet);

        // get list of movies
        List<Movie> movies = repo.saleGet(userId, saleId);
        if (movies.size() == 0) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new OrderDetailResponse()
                            .setResult(BillingResults.ORDER_DETAIL_NOT_FOUND));
        }

        // get total
        CartController cart = new CartController(this.repo, this.validate);
        BigDecimal total = cart.getCartTotal(movies, isPremiumUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new OrderDetailResponse()
                        .setTotal(total)
                        .setItems(movies)
                        .setResult(BillingResults.ORDER_DETAIL_FOUND));
    }
}
