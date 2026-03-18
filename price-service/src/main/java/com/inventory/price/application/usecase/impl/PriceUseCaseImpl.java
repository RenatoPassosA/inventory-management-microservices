package com.inventory.price.application.usecase.impl;

import com.inventory.price.application.client.ProductClient;
import com.inventory.price.application.dto.request.CreatePriceRequest;
import com.inventory.price.application.dto.request.UpdatePriceRequest;
import com.inventory.price.application.dto.response.PriceHistoryResponse;
import com.inventory.price.application.dto.response.PriceResponse;
import com.inventory.price.application.mapper.PriceMapper;
import com.inventory.price.application.usecase.PriceUseCase;
import com.inventory.price.domain.exceptions.ActivePriceAlreadyExistsException;
import com.inventory.price.domain.exceptions.InvalidPriceException;
import com.inventory.price.domain.exceptions.PriceNotFoundException;
import com.inventory.price.domain.exceptions.ProductNotFoundException;
import com.inventory.price.domain.model.Price;
import com.inventory.price.domain.repository.PriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PriceUseCaseImpl implements PriceUseCase {

    private final PriceRepository priceRepository;
    private final ProductClient productClient;

    public PriceUseCaseImpl(PriceRepository priceRepository, ProductClient productClient) {
        this.priceRepository = priceRepository;
        this.productClient = productClient;
    }

    @Override
    public PriceResponse createPrice(CreatePriceRequest request) {
        validateProductExists(request.getProductId());
        validatePriceValue(request.getPrice());

        if (priceRepository.existsActiveByProductId(request.getProductId())) {
            throw new ActivePriceAlreadyExistsException(
                    "Active price already exists for product id: " + request.getProductId()
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Price price = new Price(
                UUID.randomUUID(),
                request.getProductId(),
                request.getPrice(),
                request.getCurrency(),
                true,
                now,
                now
        );

        Price savedPrice = priceRepository.save(price);
        return PriceMapper.toResponse(savedPrice);
    }

    @Override
    public PriceResponse getCurrentPriceByProductId(UUID productId) {
        Price currentPrice = priceRepository.findActiveByProductId(productId)
                .orElseThrow(() -> new PriceNotFoundException(
                        "Active price not found for product id: " + productId
                ));

        return PriceMapper.toResponse(currentPrice);
    }

    @Override
    public PriceResponse updatePrice(UUID id, UpdatePriceRequest request) {
        validatePriceValue(request.getPrice());

        Price existingPrice = priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException(
                        "Price not found for id: " + id
                ));

        validateProductExists(existingPrice.getProductId());

        priceRepository.deactivateCurrentPrice(existingPrice.getProductId());

        LocalDateTime now = LocalDateTime.now();

        Price newPrice = new Price(
                UUID.randomUUID(),
                existingPrice.getProductId(),
                request.getPrice(),
                request.getCurrency(),
                true,
                now,
                now
        );

        Price savedPrice = priceRepository.save(newPrice);
        return PriceMapper.toResponse(savedPrice);
    }

    @Override
    public PriceHistoryResponse getPriceHistoryByProductId(UUID productId) {
        validateProductExists(productId);

        List<Price> priceHistory = priceRepository.findAllByProductId(productId);

        return new PriceHistoryResponse(
                productId,
                PriceMapper.toResponseList(priceHistory)
        );
    }

    private void validateProductExists(UUID productId) {
        boolean exists = productClient.existsById(productId);

        if (!exists) {
            throw new ProductNotFoundException(
                    "Product not found for id: " + productId
            );
        }
    }

    private void validatePriceValue(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException("Price must be greater than zero");
        }
    }
}