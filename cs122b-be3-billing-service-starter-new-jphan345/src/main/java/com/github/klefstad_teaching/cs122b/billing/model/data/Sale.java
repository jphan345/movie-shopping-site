package com.github.klefstad_teaching.cs122b.billing.model.data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

public class Sale {
    private long saleId;
    private BigDecimal total;
    private Instant orderDate;

    public long getSaleId() {
        return saleId;
    }

    public Sale setSaleId(long saleId) {
        this.saleId = saleId;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Sale setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public Sale setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
        return this;
    }
}
