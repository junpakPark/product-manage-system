package com.github.junpakpark.productmanage.product.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import com.github.junpakpark.productmanage.common.error.exception.ForbiddenException;

public class ProductFobiddenException extends ForbiddenException {
    public ProductFobiddenException(final ProductErrorCode errorCode, final Long memberId) {
        super(new ErrorCode<>(errorCode.getCode(), "%s 회원 ID: %d".formatted(errorCode.getMessage(), memberId)));
    }
}
