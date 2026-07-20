package com.xiaoliu.aiCodeMother.aop;

import com.xiaoliu.aiCodeMother.annotation.AuthCheck;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 权限校验 AOP 切面
 *
 * @author xiaoliu
 */
@Aspect
@Component
@Slf4j
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     * 拦截所有标记了 @AuthCheck 注解的方法
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable{
        // 1.获取当前请求
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 2. 获取当前用户登录信息
        User loginUser = userService.getLoginUser(request);

        // 3. 获取当前被拦截的方法名（即接口名）
        String methodName=joinPoint.getSignature().getName();
        // 获取用户的ID和角色
        Long userId = loginUser.getId();
        String userRole = loginUser.getUserRole();

        // 【作业要求1】记录：哪个用户访问了哪个接口
        log.info("用户 [ID:{}] 尝试访问接口 [{}]", userId, methodName);

        // ================== 2. 执行权限校验逻辑 ==================
        String[] anyRole = authCheck.anyRole();


        // 配置了多个角色 如果用户没有这些角色，抛出无权限异常
        if (anyRole.length > 0){
            //把数组转换成集合List直接判断用户的角色是否包含在这个列表里
            List<String> roleList = List.of(anyRole);
            if (!roleList.contains(loginUser.getUserRole())) {
                // 【作业要求3】记录：如果失败，失败原因是什么
                log.warn("用户 [ID:{}] 访问接口 [{}] 权限校验失败！用户角色为 [{}]，但要求角色为 [{}]",
                        userId, methodName, userRole, roleList);

                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 如果两个都没配置，说明只要登录即可，直接放行
        // 【作业要求2】记录：是否通过权限校验（如果走到这里，说明校验通过了）
        log.info("用户 [ID:{}] 权限校验通过，开始执行业务逻辑", userId);

        // 5. 权限校验通过，执行原方法
        return joinPoint.proceed();
    }


}
