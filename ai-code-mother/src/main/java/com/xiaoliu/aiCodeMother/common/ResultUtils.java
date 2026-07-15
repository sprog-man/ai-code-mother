package com.xiaoliu.aiCodeMother.common;

/**
 *
 * 统一工具类，封装统一响应数据格式给前端，包含成功、失败、异常三种情况
 * */

public class ResultUtils {

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    public static  BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse(errorCode);
    }

    public static BaseResponse error(ErrorCode errorCode, String message){
        return new BaseResponse(errorCode.getCode(), null, message);
    }

    public static BaseResponse error(int code, String message){
        return new BaseResponse(code, null, message);
    }
}
