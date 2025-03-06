package com.github.junpakpark.productmanage.product.command.domain;

import com.github.junpakpark.productmanage.common.domain.BaseEntity;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOption;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOptions;
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
        validateNotNull(name, price, shippingFee);
        validateDescription(description);
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
            throw new IllegalArgumentException("상품 판매자 정보가 일치하지 않습니다. (productId= %d)".formatted(this.id));
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

    private void validateNotNull(final Name name, final Money price, final Money shippingFee) {
        Objects.requireNonNull(name, "상품명은 필수입니다.");
        Objects.requireNonNull(price, "상품 가격은 필수입니다.");
        Objects.requireNonNull(shippingFee, "배송비는 필수입니다.");
    }

    private void validateDescription(final String description) {
        if (description.length() > 500) {
            throw new IllegalArgumentException("상품 설명은 최대 500자까지 가능합니다.");
        }
    }

    private void validateUpdate(final Product updatedProduct) {
        Objects.requireNonNull(updatedProduct, "수정 정보는 null일 수 없습니다.");
        validateNotNull(updatedProduct.name, updatedProduct.price, updatedProduct.shippingFee);
        validateDescription(updatedProduct.description);
    }

    private void validateMember(final Long memberId) {
        Objects.requireNonNull(memberId, "판매자 id는 필수입니다.");
        if (memberId <= 0) {
            throw new IllegalArgumentException("id는 1보다 커야합니다.");
        }
    }

}
