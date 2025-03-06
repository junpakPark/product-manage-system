package com.github.junpakpark.productmanage.product.command.application.port.in.web;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.ProductInfo;
import com.github.junpakpark.productmanage.product.command.domain.ProductName;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateProductCommand(
        @NotBlank(message = "상품명은 필수입니다.")
        String name,
        @Size(max = 500, message = "상품 설명은 500자 이하로 입력하세요.")
        String description,
        @NotNull(message = "상품 가격은 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "상품 가격은 0원보다 커야 합니다.")
        BigDecimal price,
        @NotNull(message = "배송비는 필수입니다.")
        @DecimalMin(value = "0.0", message = "배송비는 0원 이상이어야 합니다.")
        BigDecimal shippingFee
) {
    public ProductInfo toProductInfo() {
        return new ProductInfo(
                new ProductName(name),
                description,
                new Money(price),
                new Money(shippingFee)
        );
    }
}
