package com.github.junpakpark.productmanage.common.domain;

import com.github.junpakpark.productmanage.common.error.exception.ForbiddenException.RoleForbiddenException;
import lombok.Getter;

@Getter
public enum Role {

    ADMIN("관리자"),
    SELLER("판매자"),
    BUYER("구매자"),
    ;

    private final String description;

    Role(final String description) {
        this.description = description;
    }

    public void validateAdmin() {
        if (this != Role.ADMIN) {
            throw new RoleForbiddenException(Role.ADMIN);
        }
    }

    public void validateSeller() {
        if (this == Role.BUYER) {
            throw new RoleForbiddenException(Role.SELLER);
        }
    }

}
