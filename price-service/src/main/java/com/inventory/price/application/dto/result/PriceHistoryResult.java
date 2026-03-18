package com.inventory.price.application.dto.result;

import java.util.List;
import java.util.UUID;

public class PriceHistoryResult {

    private UUID productId;
    private List<PriceResult> prices;

    public PriceHistoryResult() {
    }

    public PriceHistoryResult(UUID productId, List<PriceResult> prices) {
        this.productId = productId;
        this.prices = prices;
    }

    public UUID getProductId() {
        return productId;
    }

    public List<PriceResult> getPrices() {
        return prices;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setPrices(List<PriceResult> prices) {
        this.prices = prices;
    }
}