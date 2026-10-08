package com.mall.common.exception;

import com.mall.common.api.CommonResult;
import com.mall.common.api.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public CommonResult<Void> handleApiException(ApiException exception) {
        if (exception.getErrorCode() != null) {
            return CommonResult.failed(exception.getErrorCode());
        }
        return CommonResult.failed(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError() != null
                ? exception.getBindingResult().getFieldError().getDefaultMessage()
                : ResultCode.VALIDATE_FAILED.getMessage();
        return CommonResult.failed(ResultCode.VALIDATE_FAILED, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public CommonResult<Void> handleRuntimeException(RuntimeException exception) {
        log.error("运行时异常: ", exception);
        return CommonResult.failed(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<Void> handleException(Exception exception) {
        log.error("系统异常: ", exception);
        return CommonResult.failed("系统繁忙，请稍后重试");
    }
}
