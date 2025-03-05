package com.github.junpakpark.productmanage.common.domain;

public enum Role {

    ADMIN, SELLER, BUYER,
    ;

    public boolean isAdmin() {
        return this == Role.ADMIN;
    }
}
