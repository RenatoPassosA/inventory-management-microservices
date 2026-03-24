package com.inventory.order.application.client;

import java.math.BigDecimal;
import java.util.UUID;

public interface PriceClient {

    BigDecimal getActivePriceByProductId(UUID productId);
}