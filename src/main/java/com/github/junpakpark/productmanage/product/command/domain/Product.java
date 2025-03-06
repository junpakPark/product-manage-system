package com.github.junpakpark.productmanage.product.command.domain;

import com.github.junpakpark.productmanage.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private ProductInfo productInfo;
    @Column(nullable = false)
    private Long memberId;

    public Product(final ProductInfo productInfo, final Long memberId) {
        validateProductInfo(productInfo);
        validateMember(memberId);
        this.productInfo = productInfo;
        this.memberId = memberId;
    }

    public void validateOwner(final Long ownerId) {
        if (!Objects.equals(this.memberId, ownerId)) {
            throw new IllegalArgumentException("Owner id mismatch");
        }
    }

    public void updateInfo(final ProductInfo productInfo) {
        validateProductInfo(productInfo);
        this.productInfo = productInfo;
    }

    private void validateProductInfo(final ProductInfo productInfo) {
        Objects.requireNonNull(productInfo, "상품 정보는 필수입니다.");
    }

    private void validateMember(final Long memberId) {
        Objects.requireNonNull(memberId, "판매자 id는 필수입니다.");
        if (memberId <= 0) {
            throw new IllegalArgumentException("id는 1보다 커야합니다.");
        }
    }

}
