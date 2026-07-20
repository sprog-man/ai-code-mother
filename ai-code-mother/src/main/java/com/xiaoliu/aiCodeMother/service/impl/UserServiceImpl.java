package com.xiaoliu.aiCodeMother.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaoliu.aiCodeMother.annotation.AutoFill;
import com.xiaoliu.aiCodeMother.annotation.OperationType;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import com.xiaoliu.aiCodeMother.mapper.UserMapper;
import com.xiaoliu.aiCodeMother.model.dto.user.UserQueryRequest;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.model.vo.UserVO;
import com.xiaoliu.aiCodeMother.service.UserBaseService;
import com.xiaoliu.aiCodeMother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

        // ================== 新增的校验逻辑 ==================
        // 校验1：账号只能包含字母和数字
        // Validator.isGeneral() 专门用来验证：英文字母、数字和下划线
        if (!Validator.isGeneral(userAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号只能包含字母和数字");
        }

        // 校验2 & 3：密码必须包含字母和数字，且不能包含特殊字符
        // 同样使用 isGeneral 校验，确保密码只由字母和数字组成
        if (!Validator.isGeneral(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码只能包含字母和数字");
        }
        // ================== 新增校验结束 ==================


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
        if (user.getIsDelete() == 1) {
            log.info("user login failed, user is deleted");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已封禁");
        }

        // 4. 存入 Session
        request.getSession().setAttribute(USER_LOGIN_STATE, user.getId());

        //5. 返回用户信息
        return this.getUserVO(user);


    }

    /**
     * 获取当前登录用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1. 获取 session
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 2. 判断是否存在
        if (attribute == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 3. 将拿到的 Object 强转为 Long 类型的 userId
        Long userId = (Long) attribute;

        // 4. 查询数据库获取信息
        User currentUser = userMapper.selectOneById(userId);

        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return currentUser;
    }

    /**
     * 判断用户是否登出
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        //移除登录状态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public boolean userChangePassword(String oldPassword, String newPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 1. 对比旧密码是否与数据库内的一致
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        User user = getLoginUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (!encryptedPassword.equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误");
        }
        // 2. 修改密码(验证新密码是否符合要求)
        if (!Validator.isGeneral(newPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能包含特殊字符");
        }
        user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes()));
        userBaseService.updateUser(user);
        log.info("user change password success");
        // 3. 退出登录
        userLogout(request);
        return true;

    }


    /* 用于构造脱敏用户信息 */
    @Override
    public UserVO getUserVO(User user) {
        if (user==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    /**
     * 用户分页查询(信息没有进行脱敏)
     */
    @Override
    public Page<User> listUserByPage(UserQueryRequest userQueryRequest) {
        // 1. 构建查询条件
        QueryWrapper queryWrapper = buildUserQueryWrapper(userQueryRequest);

        // 2. 分页查询
        Page<User> page=Page.of(userQueryRequest.getCurrent(),userQueryRequest.getPageSize());
        return  userMapper.paginate(page, queryWrapper);
    }

    /**
     * 用户分页查询(信息有进行脱敏)
     */
    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest){
        // 1. 查询用户分页数据
        Page<User> userPage = listUserByPage(userQueryRequest);

        // 2. 转换为 VO
        Page<UserVO> userVOPage=new Page<>(userPage.getPageNumber(), userPage.getPageSize(),userPage.getTotalPage());
        List<UserVO> userVOList=userPage.getRecords().stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return userVOPage;

    }


    /**
     * 不分页查询返回所有用户列表，用于excel表格导出
     *
     */
    @Override
    public List<User> listAllUsersByCondition(UserQueryRequest userQueryRequest) {
        List<User> users=userMapper.selectListByQuery(buildUserQueryWrapper(userQueryRequest));
        return users;
    }

    /**
     * 构建用户查询条件
     */
    private QueryWrapper buildUserQueryWrapper(UserQueryRequest userQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(USER.IS_DELETE.eq(0)); // 默认查询未删除的用户

        if (userQueryRequest == null) {
            return queryWrapper;
        }

        // ID 精确查询
        Long id = userQueryRequest.getId();
        if (id!=null){
            queryWrapper.and(USER.ID.eq(id));
        }

        // 账号模糊查询
        String userAccount = userQueryRequest.getUserAccount();
        if (StrUtil.isNotBlank(userAccount)){
            queryWrapper.and(USER.USER_ACCOUNT.like(userAccount));
        }

        // 用户名模糊查询
        String userName = userQueryRequest.getUserName();
        if (StrUtil.isNotBlank(userName)){
            queryWrapper.and(USER.USER_NAME.like(userName));
        }

        // 角色精确查询
        String userRole = userQueryRequest.getUserRole();
        if (StrUtil.isNotBlank(userRole)){
            queryWrapper.and(USER.USER_ROLE.eq(userRole));
        }

        // 排序
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        if (StrUtil.isNotBlank(sortField)){
            // 根据排序顺序添加排序条件
            if ("asc".equalsIgnoreCase(sortOrder)){
                queryWrapper.orderBy(sortField,true); //升序
            } else {
                queryWrapper.orderBy(sortField,false); //降序
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderBy(USER.CREATE_TIME,false);
        }

        return queryWrapper;
    }


}
