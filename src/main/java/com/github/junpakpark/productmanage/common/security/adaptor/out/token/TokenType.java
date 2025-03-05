package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS, REFRESH,
    ;

    public boolean isAccess() {
        return this == TokenType.ACCESS;
    }

}
