package com.xiaoliu.aiCodeMother.exception;

import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常捕获
 *
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    /*业务错误*/
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessException(BusinessException e){
        log.info("businessException: {}", e.getMessage());
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /*系统错误*/
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeException(RuntimeException e){
        log.info("runtimeException: {}", e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
