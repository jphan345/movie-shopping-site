package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.github.klefstad_teaching.cs122b.billing.model.data.Movie;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

import java.math.BigDecimal;
import java.util.List;

public class OrderDetailResponse extends ResponseModel<OrderDetailResponse> {
    private BigDecimal total;
    private List<Movie> items;

    public BigDecimal getTotal() {
        return total;
    }

    public OrderDetailResponse setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public List<Movie> getItems() {
        return items;
    }

    public OrderDetailResponse setItems(List<Movie> items) {
        this.items = items;
        return this;
    }
}
