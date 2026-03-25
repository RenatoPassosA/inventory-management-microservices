package com.inventory.order.infrastructure.client.response;

import java.math.BigDecimal;
import java.util.UUID;

public class PriceResponse {

    private UUID id;
    private UUID productId;
    private BigDecimal price;
    private String currency;
    private Boolean active;

    public PriceResponse() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public Boolean getActive() {
        return active;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}