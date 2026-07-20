package com.xiaoliu.aiCodeMother.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoliu.aiCodeMother.annotation.OperationLog;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.model.entity.UserOperationLog;
import com.xiaoliu.aiCodeMother.service.UserOperationLogService;
import com.xiaoliu.aiCodeMother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {
    @Resource
    private UserOperationLogService userOperationLogService;

    @Resource
    private UserService userService;

    // 【核心】直接注入 Spring 容器里已经创建好的 ObjectMapper
    @Resource
    private ObjectMapper objectMapper;

    @Around("@annotation(operationLog)")
    public Object recordLog(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        // 1. 准备数据：获取开始时间、接口名
        long startTime=System.currentTimeMillis();
        String methodName=joinPoint.getSignature().getName();
        String operationDesc=operationLog.value(); //获取注解里使用方法的描述

        // 2. 获取当前登录的管理员信息
        RequestAttributes requestAttribute= RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttribute).getRequest();
        User loginUser=userService.getLoginUser(request);

        // 3. 执行真正的业务逻辑（比如封禁用户）
        Object result = joinPoint.proceed();

        // 4. 业务执行成功后，计算耗时并保存日志
        long endTime=System.currentTimeMillis();
        long costTime=endTime-startTime;

        try {
            UserOperationLog logEntity = new UserOperationLog();
            logEntity.setOperatorId(loginUser.getId());
            logEntity.setOperatorName(loginUser.getUserName());
            logEntity.setOperationDesc(operationDesc);
            logEntity.setMethodName(methodName);
            // 获取请求参数（这里简单转为字符串，实际项目可以用 JSON 工具类）
            String paramsJson = objectMapper.writeValueAsString(joinPoint.getArgs());
            logEntity.setRequestParams(paramsJson);
            logEntity.setOperateTime(java.time.LocalDateTime.now());
            logEntity.setCostTime(costTime);

            // 异步或同步保存日志到数据库
            userOperationLogService.save(logEntity);
        } catch (Exception e) {
            // 记录日志失败不能影响主业务，所以这里只打印错误，不抛出异常
            log.error("记录操作日志失败", e);
        }
        return result;
    }
}
