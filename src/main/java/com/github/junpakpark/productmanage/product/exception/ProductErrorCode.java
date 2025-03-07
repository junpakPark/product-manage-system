package com.github.junpakpark.productmanage.product.exception;

import lombok.Getter;

@Getter
public enum ProductErrorCode {
    NAME_BAD_REQUEST("PRODUCT-001", "상품명은 필수입니다."),
    DESCRIPTION_BAD_REQUEST("PRODUCT-002", "상품 설명은 최대 500자까지 가능합니다."),
    PRICE_BAD_REQUEST("PRODUCT-003", "상품 가격은 필수입니다."),
    SHIPPING_FEE_BAD_REQUEST("PRODUCT-004", "배송비는 필수입니다."),
    MEMBER_BAD_REQUEST("PRODUCT-005", "잘못된 형식의 회원 id입니다."),
    UPDATE_BAD_REQUEST("PRODUCT-006", "잘못된 형식의 수정 정보입니다."),
    PRODUCT_NOT_FOUND("PRODUCT-007", "존재하지 않는 상품입니다."),
    PRODUCT_FORBIDDEN("PRODUCT-008", "상품 판매자 정보와 일치하지 않습니다."),
    NAME_LENGTH_BAD_REQUEST("PRODUCT-009", "상품명은 최대 %d자 이하여야 합니다."),
    PRICE_NEGATIVE_BAD_REQUEST("PRODUCT-010", "상품 가격은 음수일 수 없습니다."),
    ;

    private final String code;
    private final String message;

    ProductErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

}
