package com.github.junpakpark.productmanage.product.command.domain.option;

import com.github.junpakpark.productmanage.common.domain.BaseEntity;
import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.ProductConflictException;
import com.github.junpakpark.productmanage.product.exception.OptionErrorCode;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "option_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "name"))
    private Name name;
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "additional_price"))
    private Money additionalPrice;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected ProductOption(final Name name, final Money additionalPrice) {
        validateNonNull(name, additionalPrice);
        this.name = name;
        this.additionalPrice = additionalPrice;
    }

    public abstract void replaceChoices(final List<OptionChoice> optionChoices);

    public abstract List<OptionChoice> optionChoices();

    public abstract OptionType getOptionType();

    public boolean hasSameId(final Long optionId) {
        return Objects.equals(this.id, optionId);
    }

    public void update(final ProductOption productOption) {
        validateType(productOption.getOptionType());
        validateNonNull(productOption.name, productOption.additionalPrice);
        this.name = productOption.name;
        this.additionalPrice = productOption.additionalPrice;
        replaceChoices(productOption.optionChoices());
    }

    public void associatedWith(Product product) {
        validateAssociation();
        this.product = product;
    }

    public void breakAssociation() {
        this.product = null;
    }

    private void validateAssociation() {
        if (Objects.nonNull(this.product)) {
            throw new ProductConflictException(OptionErrorCode.ASSOCIATION_CONFLICT);
        }
    }

    private void validateNonNull(final Name name, final Money additionalPrice) {
        if (Objects.isNull(name)) {
            throw new ProductBadRequestException(OptionErrorCode.NAME_NULL_BAD_REQUEST);
        }
        if (Objects.isNull(additionalPrice)) {
            throw new ProductBadRequestException(OptionErrorCode.ADDITIONAL_PRICE_NULL_BAD_REQUEST);
        }
    }

    private void validateType(final OptionType optionType) {
        if (this.getOptionType().isDifferent(optionType)) {
            throw new ProductBadRequestException(OptionErrorCode.TYPE_BAD_REQUEST);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProductOption otherOption)) {
            return false;
        }
        if (Objects.isNull(getId()) || Objects.isNull(otherOption.getId())) {
            return false;
        }
        return getId().equals(otherOption.getId());
    }

    @Override
    public int hashCode() {
        return getId() == null ? super.hashCode() : getId().hashCode();
    }

}
