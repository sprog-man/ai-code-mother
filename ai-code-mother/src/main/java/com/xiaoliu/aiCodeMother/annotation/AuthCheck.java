package com.xiaoliu.aiCodeMother.annotation;

import java.lang.annotation.*;


/**
 * 权限校验注解
 * 标记在需要权限验证的方法上
 *
 * @author xiaoliu
 */
@Target(ElementType.METHOD)  // 只能用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时生效
public @interface AuthCheck {


    /**
     * 拥有任意一个即可的角色（数组）
     */
    String[] anyRole() default {};
}
