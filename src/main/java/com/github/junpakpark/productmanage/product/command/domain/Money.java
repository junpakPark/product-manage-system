package com.github.junpakpark.productmanage.product.command.domain;

import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    public Money(final BigDecimal value) {
        validateNotNull(value);
        validatePositive(value);
        this.amount = value.setScale(2, RoundingMode.HALF_UP);
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    private void validatePositive(final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductBadRequestException(ProductErrorCode.PRICE_NEGATIVE_BAD_REQUEST);
        }
    }

    private void validateNotNull(final BigDecimal amount) {
        if (amount == null) {
            throw new ProductBadRequestException(ProductErrorCode.PRICE_BAD_REQUEST);

        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Money price = (Money) o;
        return Objects.equals(amount, price.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
