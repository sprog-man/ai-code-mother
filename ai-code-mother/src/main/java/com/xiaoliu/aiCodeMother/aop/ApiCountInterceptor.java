package com.xiaoliu.aiCodeMother.aop;

import com.xiaoliu.aiCodeMother.annotation.ApiLog;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class ApiCountInterceptor {
    @Resource
    private UserService userService;

    // 线程安全的“记账本”，用来统计每个接口的访问次数
    private final ConcurrentHashMap<String, Long> apiCountMap = new ConcurrentHashMap<>();

    /**
     * 拦截所有贴了 @ApiLog 注解的方法
     */
    @Around("@annotation(apiLog)")
    public Object doApiCount(ProceedingJoinPoint joinPoint, ApiLog apiLog) throws Throwable {
        String methodName=joinPoint.getSignature().getName();
        long startTime=System.currentTimeMillis();
        boolean isSuccess=false;
        String userId="未登录用户"; // 默认值
        try {
            // 2. 【安全兜底】尝试获取当前登录用户
            try {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                HttpServletRequest request=((ServletRequestAttributes)requestAttributes).getRequest();
                User loginUser=userService.getLoginUser(request);
                userId=String.valueOf(loginUser.getId());
            } catch (Exception e) {
                // 如果获取用户失败（比如未登录、Session过期），说明是匿名访问，直接捕获异常，不影响统计
            }
            // 3. 执行原方法（真正去跑业务逻辑）
            Object result = joinPoint.proceed();
            isSuccess=true;// 没报错，说明执行成功
            return result;
        } catch (Exception e) {
            // 如果报错，保持 isSuccess = false，并继续向外抛出异常
            throw e;
        }finally {
            // 4. 【核心统计逻辑】不管成功还是失败，都要更新记账本
            long endTime=System.currentTimeMillis();
            long executionTime=endTime-startTime;// 计算执行耗时（毫秒）

            // 在 Map 中把该接口的次数 +1
            apiCountMap.put(methodName, apiCountMap.getOrDefault(methodName, 0L) + 1);

            // 5. 输出到日志
            log.info("【接口访问统计】用户 [{}] 访问接口 [{}] | 耗时: {}ms | 结果: {} | 累计次数: {}",
                    userId, methodName, executionTime, isSuccess ? "成功" : "失败", apiCountMap.get(methodName));
        }
    }
}
