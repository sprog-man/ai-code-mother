package com.xiaoliu.aiCodeMother.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义操作日志注解
 */
@Target(ElementType.METHOD) // 只能用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface OperationLog {
    /**
     * 操作描述，比如 "封禁用户"、"修改角色"
     */
    String value() default "";
}
