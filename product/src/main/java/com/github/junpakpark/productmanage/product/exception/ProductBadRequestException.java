package com.github.junpakpark.productmanage.product.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import com.github.junpakpark.productmanage.common.error.exception.BadRequestException;

public class ProductBadRequestException extends BadRequestException {
    public ProductBadRequestException(final ProductErrorCode errorCode) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage()));
    }

    public ProductBadRequestException(final OptionErrorCode errorCode) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage()));
    }

    public ProductBadRequestException(final ProductErrorCode errorCode, final int length) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage().formatted(length)));
    }

    public ProductBadRequestException(final OptionErrorCode errorCode, final int length) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage().formatted(length)));
    }
}
