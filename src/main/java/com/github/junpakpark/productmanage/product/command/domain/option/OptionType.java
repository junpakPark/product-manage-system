package com.github.junpakpark.productmanage.product.command.domain.option;

public enum OptionType {
    INPUT, SELECT,
    ;

    public boolean isDifferent(final OptionType other) {
        return this != other;
    }
}
