package com.mall.common.exception;

import com.mall.common.api.IErrorCode;

public class ApiException extends RuntimeException {

    private final IErrorCode errorCode;

    public ApiException(String message) {
        super(message);
        this.errorCode = null;
    }

    public ApiException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
