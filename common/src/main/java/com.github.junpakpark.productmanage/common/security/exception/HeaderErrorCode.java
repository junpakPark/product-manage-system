package com.github.junpakpark.productmanage.common.security.exception;

import lombok.Getter;

@Getter
public enum HeaderErrorCode {

    AUTHORIZATION_HEADER_MISSING("AUTH-001", "Authorization 헤더가 누락되었습니다."),
    AUTHORIZATION_HEADER_INVALID("AUTH-002", "Authorization 헤더의 형식에 오류가 있습니다.");

    private final String code;
    private final String message;

    HeaderErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

}
