package com.github.junpakpark.productmanage.common.domain;

public enum Role {

    ADMIN, SELLER, BUYER,
    ;

    public boolean isAdmin() {
        return this == Role.ADMIN;
    }

    public void validateSeller() {
        if (this == Role.BUYER) {
            throw new IllegalArgumentException("상품 관리 권한이 없습니다.");
        }
    }

}
