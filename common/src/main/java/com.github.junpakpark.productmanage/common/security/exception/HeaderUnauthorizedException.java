package com.github.junpakpark.productmanage.common.security.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import com.github.junpakpark.productmanage.common.error.exception.UnauthorizedException;

public class HeaderUnauthorizedException extends UnauthorizedException {
    public HeaderUnauthorizedException(final HeaderErrorCode errorCode) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage()));
    }
}
