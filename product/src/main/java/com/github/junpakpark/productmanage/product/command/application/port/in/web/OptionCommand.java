package com.github.junpakpark.productmanage.product.command.application.port.in.web;

import com.github.junpakpark.productmanage.product.command.domain.option.OptionType;
import java.math.BigDecimal;
import java.util.List;

public record OptionCommand(
        String name,
        OptionType optionType,
        BigDecimal additionalPrice,
        List<String> choices
) {
}
