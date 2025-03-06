package com.github.junpakpark.productmanage.product.command.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductName {

    private static final int MAX_LENGTH = 100;

    @Column(length = 100, nullable = false)
    private String name;


    public ProductName(final String name) {
        validateBlank(name);
        validateLength(name);
        this.name = name;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품명은 비어있을 수 없습니다.");
        }
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("상품명은 최대 %d자 이하여야 합니다.".formatted(MAX_LENGTH));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductName that = (ProductName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "ProductName= %s".formatted(name);
    }
}
