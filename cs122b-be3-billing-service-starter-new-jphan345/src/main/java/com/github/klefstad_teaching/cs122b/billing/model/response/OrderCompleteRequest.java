package com.github.klefstad_teaching.cs122b.billing.model.response;

public class OrderCompleteRequest {
    private String paymentIntentId;

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public OrderCompleteRequest setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        return this;
    }
}
