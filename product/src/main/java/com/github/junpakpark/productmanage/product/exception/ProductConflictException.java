package com.github.junpakpark.productmanage.product.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import com.github.junpakpark.productmanage.common.error.exception.ConflictException;

public class ProductConflictException extends ConflictException {

    public ProductConflictException(final ProductErrorCode errorCode) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage()));
    }

    public ProductConflictException(final OptionErrorCode errorCode) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage()));
    }

}
