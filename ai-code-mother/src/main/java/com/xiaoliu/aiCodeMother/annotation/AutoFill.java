package com.xiaoliu.aiCodeMother.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义自动填充注解
 * 标记需要自动填充公共字段的方法
 */
@Target(ElementType.METHOD) //只能作用在方法上
@Retention(RetentionPolicy.RUNTIME) //运行时生效
public @interface AutoFill {
    /**
     * 操作类型：INSERT 或 UPDATE
     */
    OperationType value();

}
