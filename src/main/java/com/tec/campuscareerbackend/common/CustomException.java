package com.tec.campuscareerbackend.common;

public class CustomException extends RuntimeException{
    private ErrorCodeEnum errorCode;
    public CustomException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public ErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}
