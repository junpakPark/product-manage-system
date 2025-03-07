package com.github.junpakpark.productmanage.common.security.exception;

import lombok.Getter;

@Getter
public enum TokenErrorCode {

    INVALID_TOKEN("TOKEN-001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("TOKEN-002", "만료된 토큰입니다."),
    NOT_ACCESS_TOKEN("TOKEN-003", "Access Token이 아닙니다."),
    ;

    private final String code;
    private final String message;

    TokenErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
