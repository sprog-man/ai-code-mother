package com.xiaoliu.aiCodeMother.aop;

import com.xiaoliu.aiCodeMother.annotation.AutoFill;
import com.xiaoliu.aiCodeMother.annotation.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;




/**
 * 自动填充切面
 * 拦截标记了 @AutoFill 注解的方法，实现自动填充公共字段功能
 */
@Slf4j
@Aspect
@Component
public class AutoFillAspect {

    /* 切入点：拦截所有标记了 @AutoFill 注解的方法 */
    @Before("@annotation(com.xiaoliu.aiCodeMother.annotation.AutoFill)")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始自动填充公共字段...");

        try {
            //1 获取方法签名
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method=methodSignature.getMethod();

            //2 获取方法上的 @AutoFill 注解
            AutoFill autoFill=method.getAnnotation(AutoFill.class);
            OperationType operationType=autoFill.value();

            //3 获取方法参数(实体对象)
            Object[] args = joinPoint.getArgs();
            if (args==null || args.length==0){
                return;
            }
            Object entity=args[0];

            //4 准备要填充的数据
            Date now=new Date();

            //5 根据操作类型填充字段
            if (operationType==OperationType.INSERT){
                //插入操作：填充创建时间和更新时间
                setFieldValue(entity, "setCreateTime", now);
                setFieldValue(entity, "setUpdateTime", now);
                log.info("自动填充创建时间和更新时间");
            }else if (operationType==OperationType.UPDATE){
                // 更新操作：只填充更新时间
                setFieldValue(entity, "setUpdateTime", now);
                log.info("自动填充更新时间");
            }
        } catch (Exception e) {
            log.error("自动填充字段出错：{}", e.getMessage());
        }
    }

    /**
     * 通过反射设置字段值
     */
    private void setFieldValue(Object entity, String methodName, Date value) {
        try{
            Method method=entity.getClass().getMethod(methodName, value.getClass());
            method.invoke(entity, value);
        } catch (Exception e) {
            log.error("设置字段值失败：{}", e.getMessage());
        }
    }
}