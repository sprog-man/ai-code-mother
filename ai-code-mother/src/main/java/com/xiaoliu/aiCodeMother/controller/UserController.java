package com.xiaoliu.aiCodeMother.controller;

import com.mybatisflex.core.query.QueryWrapper;
import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import com.xiaoliu.aiCodeMother.mapper.UserMapper;
import com.xiaoliu.aiCodeMother.model.dto.user.UserLoginRequest;
import com.xiaoliu.aiCodeMother.model.dto.user.UserRegisterRequest;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.model.vo.UserVO;
import com.xiaoliu.aiCodeMother.service.UserBaseService;
import com.xiaoliu.aiCodeMother.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.xiaoliu.aiCodeMother.model.entity.table.UserTableDef.USER;

@RestController
@RequestMapping("/user")
@Tag(name = "用户接口",description = "用户的增删改查")
public class UserController {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private UserBaseService userBaseService;

    // ========== 注册与登录 接口 ==========
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest request){
        if (request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount= request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        String userName= request.getUserName();
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId=userService.userRegister(userAccount, userPassword, checkPassword, userName);
        return  ResultUtils.success(userId);

    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录接口")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest request, HttpServletRequest httpServletRequest){
        if (request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount=request.getUserAccount();
        String userPassword=request.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO userVO = userService.userLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(userVO);
    }

    // 普通接口==========================

    /* 查询所有用户 */
    @GetMapping("/list")
    @Operation(summary = "查询所有用户")
    public BaseResponse<List<User>> listUsers(){
        List<User> users=userMapper.selectAll();
        return ResultUtils.success(users);
    }

    /* 根据ID查询用户 */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    public BaseResponse<User> getUserById(@PathVariable Long id){
        User user=userMapper.selectOneById(id);
        return ResultUtils.success(user);
    }

    /* 新增用户 */
    @PostMapping("/add")
    @Operation(summary = "新增用户")
    public BaseResponse<Long> addUser(@RequestBody User user){
        userBaseService.insertUser(user);
        return ResultUtils.success(user.getId());
    }

    /* 更新用户 */
    @PostMapping("/update")
    @Operation(summary = "更新用户")
    public BaseResponse<Boolean> updateUser(@RequestBody User user){
        int rows = userBaseService.updateUser(user);
        return ResultUtils.success(rows > 0);
    }

    /* 删除用户 */
    @PostMapping("/delete/{id}")
    @Operation(summary = "删除用户")
    public BaseResponse<Boolean> deleteUser(@PathVariable Long id){
        int rows = userMapper.deleteById(id);
        return ResultUtils.success(rows > 0);
    }

    /**
     * 根据账号查询用户
     */
    @GetMapping("/getByAccount")
    @Operation(summary = "根据账号查询用户")
    public BaseResponse<User> getUserByAccount(@RequestParam String userAccount) {
        QueryWrapper query = QueryWrapper.create()
                .where(USER.USER_ACCOUNT.eq(userAccount))
                .and(USER.IS_DELETE.eq(0));

        User user = userMapper.selectOneByQuery(query);
        return ResultUtils.success(user);
    }

    /**
     * 查询管理员列表
     */
    @GetMapping("/listAdmin")
    @Operation(summary = "查询管理员列表")
    public BaseResponse<List<User>> listAdmins() {
        QueryWrapper query = QueryWrapper.create()
                .where(USER.USER_ROLE.eq("admin"))
                .and(USER.IS_DELETE.eq(0));

        List<User> users = userMapper.selectListByQuery(query);
        return ResultUtils.success(users);
    }



}
