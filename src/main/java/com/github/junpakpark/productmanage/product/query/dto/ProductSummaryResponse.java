package com.github.junpakpark.productmanage.product.query.dto;

import com.github.junpakpark.productmanage.product.command.domain.Product;
import java.math.BigDecimal;

public record ProductSummaryResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal shippingFee
) {
    public static ProductSummaryResponse from(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName().getValue(),
                product.getDescription(),
                product.getPrice().getAmount(),
                product.getShippingFee().getAmount()
        );
    }
}

