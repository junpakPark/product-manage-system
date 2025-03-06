package com.github.junpakpark.productmanage.product.command.domain.option;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
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

    public SelectOption(final Name name, final Money additionalPrice) {
        super(name, additionalPrice);
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

    private void validateChoices(final List<OptionChoice> optionChoices) {
        Objects.requireNonNull(optionChoices, "optionValues는 null일 수 없습니다.");
        if (optionChoices.isEmpty()) {
            throw new IllegalArgumentException("선택지가 적어도 하나는 있어야합니다.");
        }
    }

}
