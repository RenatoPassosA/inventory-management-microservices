package com.inventory.price.application.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public class CreatePriceCommand {

    private UUID productId;
    private BigDecimal price;
    private String currency;

    public CreatePriceCommand() {
    }

    public CreatePriceCommand(UUID productId, BigDecimal price, String currency) {
        this.productId = productId;
        this.price = price;
        this.currency = currency;
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

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}