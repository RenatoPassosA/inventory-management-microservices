package com.inventory.price.application.dto.request;

import java.math.BigDecimal;

public class UpdatePriceRequest {

    private BigDecimal price;
    private String currency;

    public UpdatePriceRequest(BigDecimal price, String currency) {
        this.price = price;
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}