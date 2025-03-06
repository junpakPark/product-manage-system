package com.github.junpakpark.productmanage.product.query.dto;

import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOption;
import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal shippingFee,
        List<ProductOptionResponse> options
) {
    public static ProductDetailResponse from(final Product product) {

        return new ProductDetailResponse(
                product.getId(),
                product.getName().getValue(),
                product.getDescription(),
                product.getPrice().getAmount(),
                product.getShippingFee().getAmount(),
                getOptions(product.getProductOptions())
        );
    }

    private static List<ProductOptionResponse> getOptions(final List<ProductOption> productOptions) {
        return productOptions.stream()
                .map(ProductOptionResponse::from)
                .toList();
    }
}

