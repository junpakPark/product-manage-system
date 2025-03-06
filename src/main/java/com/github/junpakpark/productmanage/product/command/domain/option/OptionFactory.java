package com.github.junpakpark.productmanage.product.command.domain.option;

import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionChoiceCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionCommand;
import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OptionFactory {

    public ProductOption createOption(final OptionCommand command) {
        return switch (command.optionType()) {
            case INPUT -> new InputOption(new Name(command.name()), new Money(command.additionalPrice()));
            case SELECT -> new SelectOption(
                    new Name(command.name()),
                    new Money(command.additionalPrice()),
                    createOptionChoice(command.choices())
            );
        };
    }

    private List<OptionChoice> createOptionChoice(List<OptionChoiceCommand> choiceCommands) {
        return choiceCommands.stream()
                .map(OptionChoiceCommand::value)
                .map(this::toOptionChoice)
                .toList();
    }

    private OptionChoice toOptionChoice(String choice) {
        return new OptionChoice(choice);
    }

}
