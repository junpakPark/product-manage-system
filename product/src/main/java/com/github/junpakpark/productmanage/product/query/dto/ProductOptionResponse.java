package com.github.junpakpark.productmanage.product.query.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.junpakpark.productmanage.product.command.domain.option.OptionChoice;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOption;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ProductOptionResponse(
        Long id,
        String name,
        BigDecimal additionalPrice,
        String optionType,
        List<String> choices
) {

    public static ProductOptionResponse from(final ProductOption option) {
        return new ProductOptionResponse(
                option.getId(),
                option.getName().getValue(),
                option.getAdditionalPrice().getAmount(),
                option.getOptionType().name(),
                Collections.emptyList()
        );
    }

    public static ProductOptionResponse of(final ProductOption option, final List<OptionChoice> choices) {
        return new ProductOptionResponse(
                option.getId(),
                option.getName().getValue(),
                option.getAdditionalPrice().getAmount(),
                option.getOptionType().name(),
                getChoices(choices)
        );
    }

    private static List<String> getChoices(final List<OptionChoice> optionChoices) {
        return optionChoices.stream()
                .sorted(Comparator.comparing(OptionChoice::getValue))
                .map(OptionChoice::getValue)
                .toList();
    }

}

