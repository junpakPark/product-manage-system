package com.github.junpakpark.productmanage.product.command.domain.option;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.OptionErrorCode;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SELECT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectOption extends ProductOption {

    @ElementCollection
    @CollectionTable(name = "option_choice", joinColumns = @JoinColumn(name = "select_option_id"))
    private Set<OptionChoice> choices = new HashSet<>();

    public SelectOption(final Name name, final Money additionalPrice, final List<OptionChoice> optionChoices) {
        super(name, additionalPrice);
        replaceChoices(optionChoices);
    }

    @Override
    public void replaceChoices(final List<OptionChoice> optionChoices) {
        validateChoices(optionChoices);
        this.choices = new HashSet<>(optionChoices);
    }

    @Override
    public List<OptionChoice> optionChoices() {
        return choices.stream()
                .sorted(Comparator.comparing(OptionChoice::getValue))
                .toList();
    }

    @Override
    public OptionType getOptionType() {
        return OptionType.SELECT;
    }

    private void validateChoices(final List<OptionChoice> optionChoices) {
        if (Objects.isNull(optionChoices)) {
            throw new ProductBadRequestException(OptionErrorCode.SELECT_CHOICES_NULL_BAD_REQUEST);
        }
        if (optionChoices.isEmpty()) {
            throw new ProductBadRequestException(OptionErrorCode.SELECT_CHOICES_EMPTY_BAD_REQUEST);
        }
    }

}
