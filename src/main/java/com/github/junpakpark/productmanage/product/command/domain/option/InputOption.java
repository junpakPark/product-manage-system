package com.github.junpakpark.productmanage.product.command.domain.option;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import com.github.junpakpark.productmanage.product.exception.ProductConflictException;
import com.github.junpakpark.productmanage.product.exception.OptionErrorCode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("INPUT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InputOption extends ProductOption {

    public InputOption(final Name name, final Money additionalPrice) {
        super(name, additionalPrice);
    }

    @Override
    public void replaceChoices(final List<OptionChoice> optionChoices) {
        if (Objects.nonNull(optionChoices) && !optionChoices.isEmpty()) {
            throw new ProductConflictException(OptionErrorCode.INPUT_OPTION_CONFLICT);
        }
    }

    @Override
    public List<OptionChoice> optionChoices() {
        return Collections.emptyList();
    }

    @Override
    public OptionType getOptionType() {
        return OptionType.INPUT;
    }

}
