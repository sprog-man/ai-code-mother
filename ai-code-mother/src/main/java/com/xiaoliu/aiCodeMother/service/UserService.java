package com.xiaoliu.aiCodeMother.service;

import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;

public interface UserService {
    /**
     * 用户注册
     *
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @param userName      用户名
     * @return 新用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userName);

    /**
     * 用户登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request      请求对象
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取脱敏的用户信息
     *
     * @param user 原始用户对象
     * @return 脱敏后的用户信息
     */
    UserVO getUserVO(User user);
}
