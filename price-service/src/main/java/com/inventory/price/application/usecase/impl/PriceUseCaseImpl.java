package com.inventory.price.application.usecase.impl;


import com.inventory.price.application.client.ProductClient;
import com.inventory.price.application.dto.command.CreatePriceCommand;
import com.inventory.price.application.dto.command.UpdatePriceCommand;
import com.inventory.price.application.dto.result.PriceHistoryResult;
import com.inventory.price.application.dto.result.PriceResult;
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
    public PriceResult createPrice(CreatePriceCommand command) {
        validateProductExists(command.getProductId());
        validatePriceValue(command.getPrice());

        if (priceRepository.existsActiveByProductId(command.getProductId())) {
            throw new ActivePriceAlreadyExistsException(
                    "Active price already exists for product id: " + command.getProductId()
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Price price = new Price(
                UUID.randomUUID(),
                command.getProductId(),
                command.getPrice(),
                command.getCurrency(),
                true,
                now,
                now
        );

        Price savedPrice = priceRepository.save(price);
        return PriceMapper.toResult(savedPrice);
    }

    @Override
    public PriceResult getCurrentPriceByProductId(UUID productId) {
        Price currentPrice = priceRepository.findActiveByProductId(productId)
                .orElseThrow(() -> new PriceNotFoundException(
                        "Active price not found for product id: " + productId
                ));

        return PriceMapper.toResult(currentPrice);
    }

    @Override
    public PriceResult updatePrice(UUID id, UpdatePriceCommand command) {
        validatePriceValue(command.getPrice());

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
                command.getPrice(),
                command.getCurrency(),
                true,
                now,
                now
        );

        Price savedPrice = priceRepository.save(newPrice);
        return PriceMapper.toResult(savedPrice);
    }

    @Override
    public PriceHistoryResult getPriceHistoryByProductId(UUID productId) {
        validateProductExists(productId);

        List<Price> priceHistory = priceRepository.findAllByProductId(productId);

        return new PriceHistoryResult(
                productId,
                PriceMapper.toResultList(priceHistory)
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