package com.xiaoliu.aiCodeMother.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.xiaoliu.aiCodeMother.annotation.AutoFill;
import com.xiaoliu.aiCodeMother.annotation.OperationType;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import com.xiaoliu.aiCodeMother.mapper.UserMapper;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.model.vo.UserVO;
import com.xiaoliu.aiCodeMother.service.UserBaseService;
import com.xiaoliu.aiCodeMother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.xiaoliu.aiCodeMother.model.entity.table.UserTableDef.USER;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserBaseService userBaseService;

    /**
     * 盐值（加密密码的混淆串）
     */
    private static final String SALT = "xiaoliu";

    /**
     * 用户登录态键
     */
    private static final String USER_LOGIN_STATE = "user_login";


    // 用户注册
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String userName) {
        // 1. 校验参数
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于4位");
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于6位");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

        // 2. 检查账号是否存在
        QueryWrapper queryWrapper=QueryWrapper.create()
                .where(USER.USER_ACCOUNT.eq(userAccount));
        long count=userMapper.selectCountByQuery(queryWrapper);
        if (count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 3. 加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 4. 插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        user.setUserName(userName);
        user.setUserRole("user");

        int result = userBaseService.insertUser(user);
        if (result<=0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }

        return user.getId();

    }

    /**
     * 用户登录
     */
    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于4位");
        }
        if (userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于6位");
        }

        // 2. 加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 查询用户
        QueryWrapper queryWrapper=QueryWrapper.create()
                .where(USER.USER_ACCOUNT.eq(userAccount))
                .and(USER.USER_PASSWORD.eq(encryptedPassword))
                .and(USER.IS_DELETE.eq(0));

        User user=userMapper.selectOneByQuery(queryWrapper);
        if (user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 4. 存入 Session
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        //5. 返回用户信息
        return this.getUserVO(user);


    }

    @Override
    public UserVO getUserVO(User user) {
        if (user==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

}
