package com.github.junpakpark.productmanage.product.command.domain;

import com.github.junpakpark.productmanage.common.domain.BaseEntity;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOption;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOptions;
import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductFobiddenException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "name"))
    private Name name;
    @Column(length = 500)
    private String description;
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Money price;
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "shipping_fee"))
    private Money shippingFee;
    @Column(nullable = false)
    private Long memberId;
    @Embedded
    private ProductOptions productOptions;

    public Product(
            final Name name,
            final String description,
            final Money price,
            final Money shippingFee,
            final Long memberId
    ) {
        validate(name, description, price, shippingFee);
        validateMember(memberId);
        this.name = name;
        this.description = description;
        this.price = price;
        this.shippingFee = shippingFee;
        this.memberId = memberId;
        this.productOptions = new ProductOptions();
    }

    public void validateOwner(final Long ownerId) {
        if (!Objects.equals(this.memberId, ownerId)) {
            throw new ProductFobiddenException(ProductErrorCode.PRODUCT_FORBIDDEN, ownerId);
        }
    }

    public void update(final Product updatedProduct) {
        validateUpdate(updatedProduct);
        this.name = updatedProduct.name;
        this.description = updatedProduct.description;
        this.price = updatedProduct.price;
        this.shippingFee = updatedProduct.shippingFee;
    }

    public void addOption(final ProductOption productOption) {
        productOption.associatedWith(this);
        this.productOptions.add(productOption);
    }

    public void removeOption(final Long optionId) {
        this.productOptions.removeById(optionId);
    }

    public void updateOption(final Long optionId, final ProductOption updateOption) {
        this.productOptions.update(optionId, updateOption);
    }

    public List<ProductOption> getProductOptions() {
        return productOptions.getOptions();
    }

    private void validate(final Name name, final String description, final Money price, final Money shippingFee) {
        if (Objects.isNull(name)) {
            throw new ProductBadRequestException(ProductErrorCode.NAME_BAD_REQUEST);
        }
        if (description.length() > 500) {
            throw new ProductBadRequestException(ProductErrorCode.DESCRIPTION_BAD_REQUEST);
        }
        if (Objects.isNull(price)) {
            throw new ProductBadRequestException(ProductErrorCode.PRICE_BAD_REQUEST);
        }
        if (Objects.isNull(shippingFee)) {
            throw new ProductBadRequestException(ProductErrorCode.SHIPPING_FEE_BAD_REQUEST);
        }
    }

    private void validateUpdate(final Product updatedProduct) {
        if (Objects.isNull(updatedProduct)) {
            throw new ProductBadRequestException(ProductErrorCode.UPDATE_BAD_REQUEST);
        }
        validate(updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice(),
                updatedProduct.getShippingFee());
    }

    private void validateMember(final Long memberId) {
        if (Objects.isNull(memberId) || memberId < 1) {
            throw new ProductBadRequestException(ProductErrorCode.MEMBER_BAD_REQUEST);
        }
    }
}
