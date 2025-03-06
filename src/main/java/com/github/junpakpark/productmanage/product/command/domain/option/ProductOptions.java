package com.github.junpakpark.productmanage.product.command.domain.option;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class ProductOptions {

    private static final int MAX_OPTIONS = 3;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductOption> options = new HashSet<>();

    public void add(final ProductOption productOption) {
        validateSize();
        options.add(productOption);
        validateDuplicateOptionName();
    }

    public void removeById(final Long optionId) {
        final ProductOption option = findById(optionId);
        option.breakAssociation();
        options.remove(option);
    }

    public void update(final Long optionId, final ProductOption updateOption) {
        final ProductOption option = findById(optionId);
        option.update(updateOption);
        validateDuplicateOptionName();
    }

    public List<ProductOption> getOptions() {
        return options.stream()
                .sorted(Comparator.comparing(ProductOption::getId))
                .toList();
    }

    private void validateSize() {
        if (options.size() >= MAX_OPTIONS) {
            throw new IllegalArgumentException("옵션은 %d개를 초과할 수 없습니다.".formatted(MAX_OPTIONS));
        }
    }

    private ProductOption findById(final Long optionId) {
        return options.stream()
                .filter(option -> option.hasSameId(optionId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private void validateDuplicateOptionName() {
        final long distinctCount = options.stream()
                .map(ProductOption::getName)
                .distinct()
                .count();

        if (distinctCount != options.size()) {
            throw new IllegalArgumentException("동일한 이름의 옵션이 이미 존재합니다.");
        }
    }

}
