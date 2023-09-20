package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

import java.util.List;

public class OrderListResponse extends ResponseModel<OrderListResponse> {
    private List<Sale> sales;

    public List<Sale> getSales() {
        return sales;
    }

    public OrderListResponse setSales(List<Sale> sales) {
        this.sales = sales;
        return this;
    }
}
