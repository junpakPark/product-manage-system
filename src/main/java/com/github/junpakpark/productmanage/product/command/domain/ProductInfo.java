package com.github.junpakpark.productmanage.product.command.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInfo {

    @Embedded
    private ProductName name;
    @Column(length = 500)
    private String description;
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Money price;
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "shipping_fee"))
    private Money shippingFee;

    public ProductInfo(
            final ProductName name,
            final String description,
            final Money price,
            final Money shippingFee
    ) {
        validateNotNull(name, price, shippingFee);
        validateDescription(description);
        this.name = name;
        this.description = description;
        this.price = price;
        this.shippingFee = shippingFee;
    }

    private void validateNotNull(final ProductName name, final Money price, final Money shippingFee) {
        Objects.requireNonNull(name, "상품명은 필수입니다.");
        Objects.requireNonNull(price, "상품 가격은 필수입니다.");
        Objects.requireNonNull(shippingFee, "배송비는 필수입니다.");
    }

    private void validateDescription(final String description) {
        if (description.length() > 500) {
            throw new IllegalArgumentException("상품 설명은 최대 500자까지 가능합니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductInfo that = (ProductInfo) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description)
                && Objects.equals(price, that.price) && Objects.equals(shippingFee, that.shippingFee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price, shippingFee);
    }

    @Override
    public String toString() {
        return "ProductInfo{" +
                "name=" + name +
                ", price=" + price +
                ", shippingFee=" + shippingFee + '\'' +
                ", description='" + description +
                '}';
    }
}
