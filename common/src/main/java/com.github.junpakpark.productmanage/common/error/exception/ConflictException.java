package com.github.junpakpark.productmanage.common.error.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class ConflictException extends GlobalException {

    public ConflictException(ErrorCode<?> errorCode) {
        super(errorCode, HttpStatus.CONFLICT);
    }

}
