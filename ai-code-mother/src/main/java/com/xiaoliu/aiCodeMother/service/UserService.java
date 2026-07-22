package com.xiaoliu.aiCodeMother.service;

import com.mybatisflex.core.paginate.Page;
import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.model.dto.user.UserQueryRequest;
import com.xiaoliu.aiCodeMother.model.dto.user.UserUpdateMyRequest;
import com.xiaoliu.aiCodeMother.model.dto.user.UserUpdatePasswordRequest;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * 获取当前登录用户
     *
     * @param request 请求对象
     * @return 当前用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登出
     *
     * @param request 请求对象
     * @return 是否成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 用户修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    boolean userChangePassword(String oldPassword, String newPassword, HttpServletRequest request);


    /**
     * 获取脱敏的用户信息
     *
     * @param user 原始用户对象
     * @return 脱敏后的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 分页查询用户列表（仅管理员）
     *
     * @param userQueryRequest 查询条件
     * @return 分页结果
     */
    Page<User> listUserByPage(UserQueryRequest userQueryRequest);

    /**
     * 分页查询用户列表（返回脱敏后的 VO）
     *
     * @param userQueryRequest 查询条件
     * @return 分页结果
     */
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    /**
     * 不分页查询返回所有用户列表，用于excel表格导出
     * @param userQueryRequest 查询条件
     * @return 用户列表
     */
    List<User> listAllUsersByCondition(UserQueryRequest userQueryRequest);

    /**
     * 修改个人信息
     *
     * @param userUpdateMyRequest 修改信息
     * @param request             请求对象
     * @return 是否成功
     */
    boolean updateMyInfo(UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request);

    /**
     * 修改密码
     *
     * @param userUpdatePasswordRequest 修改密码信息
     * @param request                   请求对象
     * @return 是否成功
     */
    boolean updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest, HttpServletRequest request);

    /**
     * 用户更新头像
     */
    // 当用户更新头像后，必须删除 Redis 里的旧缓存，保证下次查询时能拿到最新的头像 URL
    //rollbackFor = Exception.class 这句话的意思就是：
    //“Spring 你给我听好了！不管代码里抛出什么类型的异常（哪怕是普通的 Exception），只要出错了，就立刻给我无条件回滚！”
    @CacheEvict(cacheNames = "user", key = "#loginUser.id")
    @Transactional(rollbackFor = Exception.class)
     String updateUserAvatar(User loginUser, MultipartFile file);
}
