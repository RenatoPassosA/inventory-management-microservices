package com.inventory.price.application.dto.command;

import java.math.BigDecimal;

public class UpdatePriceCommand {

    private BigDecimal price;
    private String currency;

    public UpdatePriceCommand() {
    }

    public UpdatePriceCommand(BigDecimal price, String currency) {
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