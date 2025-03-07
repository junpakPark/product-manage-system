package com.github.junpakpark.productmanage.product.command.domain;

import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {

    private static final int MAX_LENGTH = 100;

    @Column(length = 100, nullable = false)
    private String value;


    public Name(final String value) {
        validateBlank(value);
        validateLength(value);
        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new ProductBadRequestException(ProductErrorCode.NAME_BAD_REQUEST);
        }
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_LENGTH) {
            throw new ProductBadRequestException(ProductErrorCode.NAME_LENGTH_BAD_REQUEST, MAX_LENGTH);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Name that = (Name) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "Name {%s}".formatted(value);
    }
}
