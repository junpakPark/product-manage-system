package com.github.junpakpark.productmanage.product.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import com.github.junpakpark.productmanage.common.error.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(final ProductErrorCode errorCode, final Long productId) {
        super(new ErrorCode<>(errorCode.getCode(), "%s 상품 ID: %d".formatted(errorCode.getMessage(), productId)));
    }

}
