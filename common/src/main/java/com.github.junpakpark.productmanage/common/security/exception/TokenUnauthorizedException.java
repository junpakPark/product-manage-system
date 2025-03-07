package com.github.junpakpark.productmanage.common.security.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import com.github.junpakpark.productmanage.common.error.exception.UnauthorizedException;

public class TokenUnauthorizedException extends UnauthorizedException {

    public TokenUnauthorizedException(final TokenErrorCode errorCode) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage()));
    }

    public TokenUnauthorizedException(final TokenErrorCode errorCode, final String memberId) {
        super(new ErrorCode<>(errorCode.getCode(), "%s 사용자 ID: %s".formatted(errorCode.getMessage(), memberId)));
    }

}
