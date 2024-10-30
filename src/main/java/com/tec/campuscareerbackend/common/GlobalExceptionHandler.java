package com.tec.campuscareerbackend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(CustomException ex) {
        // 获取错误码和错误消息
        ErrorCodeEnum errorCode = ex.getErrorCode();
        String errorMessage = ex.getMessage();

        // 创建 ErrorResponse 对象
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorMessage);

        // 返回 ResponseEntity 包装的 ErrorResponse 对象，并设置状态码
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(999, e.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
}
