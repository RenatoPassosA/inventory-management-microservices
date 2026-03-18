package com.inventory.price.adapters.input.controller.response;

import java.util.List;
import java.util.UUID;

public class PriceHistoryResponse {

    private UUID productId;
    private List<PriceResponse> prices;

    public PriceHistoryResponse(UUID productId, List<PriceResponse> prices) {
        this.productId = productId;
        this.prices = prices;
    }

    public UUID getProductId() {
        return productId;
    }

    public List<PriceResponse> getPrices() {
        return prices;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setPrices(List<PriceResponse> prices) {
        this.prices = prices;
    }
}