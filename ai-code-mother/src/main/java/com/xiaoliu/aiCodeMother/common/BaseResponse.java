package com.xiaoliu.aiCodeMother.common;

import lombok.Data;

import java.io.Serializable;

/*
* @Data 注解的作用
@Data 是 Lombok 库中最常用的“全家桶”注解。当它放在一个类上时，Lombok 会在编译阶段自动为这个类生成以下 5 种常用的方法，从而让你省去写大量样板代码的麻烦：
@Getter：为所有字段自动生成 get 方法。
@Setter：为所有非 final 字段自动生成 set 方法。
@ToString：自动生成 toString() 方法，方便在打印日志或调试时直接看到对象里的字段值。
@EqualsAndHashCode：自动生成 equals() 和 hashCode() 方法，方便进行对象比较。
@RequiredArgsConstructor：自动生成一个包含所有 final 字段和 @NonNull 字段的构造器。
* 统一响应类
* */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private T data;
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data){
        this(code,data,"");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null, errorCode.getMessage());
    }

}
