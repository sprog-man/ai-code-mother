package com.xiaoliu.aiCodeMother.common;

import lombok.Getter;

/*
*@Getter注解是lombok库中的一个注解，用于自动生成getter方法。
* 当我们在枚举类中定义了成员变量（字段）时，lombok的@Getter注解会自动为该枚举类中所有的成员变量（字段）生成对应的公共 getter 方法。
* 统一错误码
*/
@Getter
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "没有权限"),
    NO_FOUND_ERROR(40400,"请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    TOO_MANY_REQUESTS_ERROR(42900, "请求过于频繁"),
    SYSTEM_ERROR(50000, "系统错误"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
