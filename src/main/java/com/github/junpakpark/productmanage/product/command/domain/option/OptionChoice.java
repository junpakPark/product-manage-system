package com.github.junpakpark.productmanage.product.command.domain.option;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionChoice {

    private static final int MAX_LENGTH = 30;

    @Column(name = "choice_value", nullable = false, length = 30)
    private String value;

    public OptionChoice(final String value) {
        validateBlank(value);
        validateLength(value);
        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("선택지는 비어있을 수 없습니다.");
        }
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("선택지는 최대 %d자 이하여야 합니다.".formatted(MAX_LENGTH));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OptionChoice that = (OptionChoice) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "OptionChoice {%s}".formatted(value);
    }

}
