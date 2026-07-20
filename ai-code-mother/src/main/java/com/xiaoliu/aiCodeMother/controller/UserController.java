package com.xiaoliu.aiCodeMother.controller;

import com.alibaba.excel.EasyExcel;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain; // 记得导入这个类
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.update.UpdateWrapper;
import com.xiaoliu.aiCodeMother.annotation.ApiLog;
import com.xiaoliu.aiCodeMother.annotation.AuthCheck;
import com.xiaoliu.aiCodeMother.annotation.OperationLog;
import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import com.xiaoliu.aiCodeMother.mapper.UserMapper;
import com.xiaoliu.aiCodeMother.model.dto.user.*;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.model.vo.UserExcelVO;
import com.xiaoliu.aiCodeMother.model.vo.UserVO;
import com.xiaoliu.aiCodeMother.service.UserBaseService;
import com.xiaoliu.aiCodeMother.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // ========== 用户个人信息接口 ==========
    /**
     * 获取当前登录用户（完整信息）
     */
    @GetMapping("/get/my")
    @Operation(summary = "获取当前登录用户（完整信息）")
    @AuthCheck
    public BaseResponse<User> getMyInfo(HttpServletRequest request){
        User user=userService.getLoginUser(request);
        return ResultUtils.success(user);
    }

    /**
     * 修改个人信息
     */
    @PostMapping("/update/my")
    @Operation(summary = "修改个人信息")
    @AuthCheck
    public BaseResponse<Boolean> updateMyInfo(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
                                              HttpServletRequest request){
        if (userUpdateMyRequest ==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result=userService.updateMyInfo(userUpdateMyRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 修改密码
     */
    @PostMapping("/update/password")
    @Operation(summary = "修改密码")
    @AuthCheck
    public BaseResponse<Boolean> updatePassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest,
                                                HttpServletRequest request){
        if (userUpdatePasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result=userService.updatePassword(userUpdatePasswordRequest, request);
        return ResultUtils.success(result);
    }

    // ========== 分页查询接口 ==========
    /**
     * 用户分页查询(数据未过敏)
     */
    @PostMapping("/list/page")
    @Operation(summary = "用户分页查询")
    @AuthCheck(anyRole = {"admin", "vip"})
    @ApiLog
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest){
        Page<User> userPage = userService.listUserByPage(userQueryRequest);
        return ResultUtils.success(userPage);
    }

    /**
     * 用户分页查询(数据已过敏)
     */
    @PostMapping ("/list/page/sensitive")
    @Operation(summary = "用户分页查询(数据已过敏)")
    @AuthCheck(anyRole = {"admin", "vip"})
    @ApiLog
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest){
        Page<UserVO> userPage = userService.listUserVOByPage(userQueryRequest);
        return ResultUtils.success(userPage);
    }

    // ========== 管理员操作接口 ==========

    /**
     * 管理员更新用户信息
     */
    @PostMapping("/update")
    @Operation(summary = "更新用户信息")
    @AuthCheck(anyRole = {"admin","vip"})
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest){
        // 1. 判断请求是否有传入
        if (userUpdateRequest ==null || userUpdateRequest.getId()==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 判断用户是否存在
        User user=userMapper.selectOneById(userUpdateRequest.getId());
        if (user==null){
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR,"用户不存在");
        }

        User updateUser=new User();
        updateUser.setId(userUpdateRequest.getId());
        updateUser.setUserName(userUpdateRequest.getUserName());
        updateUser.setUserAvatar(userUpdateRequest.getUserAvatar());
        updateUser.setUserProfile(userUpdateRequest.getUserProfile());
        updateUser.setUserRole(userUpdateRequest.getUserRole());

        int rows= userBaseService.updateUser(updateUser);
        return ResultUtils.success(rows>0);
    }

    /**
     * 封禁用户（逻辑删除）
     */
    @PostMapping("/ban/{id}")
    @Operation(summary = "封禁用户")
    @AuthCheck(anyRole = {"admin"})
    @OperationLog("封禁用户")
    public BaseResponse<Boolean> banUser(@PathVariable Long id){
        if (id==null || id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1. 查询用户是否存在
        User user=userMapper.selectOneById(id);
        if (user==null){
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR,"用户不存在");
        }

        // 2.不能封禁管理员
        if ("admin".equals(user.getUserRole())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能封禁管理员");
        }

        // 3. 封禁用户(逻辑删除)
        User updateUser=new User();
        updateUser.setId(id);
        updateUser.setIsDelete(1);

        int rows= userBaseService.updateUser(updateUser);
        return ResultUtils.success(rows>0);
    }

    /**
     * 解封用户（取消逻辑删除）
     */
    @PostMapping("/unban/{id}")
    @Operation(summary = "解封用户")
    @AuthCheck(anyRole = {"admin"})
    public BaseResponse<Boolean> unbanUser(@PathVariable Long id){
        if (id==null || id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1. 查询用户是否存在
        User user=userMapper.selectOneById(id);
        if (user==null){
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR,"用户不存在");
        }

        // 2. 解封用户（取消逻辑删除）
        User updateUser=new User();
        updateUser.setId(id);
        updateUser.setIsDelete(0);

        int rows= userBaseService.updateUser(updateUser);
        return ResultUtils.success(rows>0);
    }

    // ========== 批量操作 接口 ==========

    /**
     * 批量封禁用户
     */
    @PostMapping("/ban/batch")
    @AuthCheck(anyRole = {"admin"})
    @Operation(summary = "批量封禁用户")
    public BaseResponse<Boolean> banUsers(@RequestBody List<Long> ids){
        // 1. 参数校验
        if (ids==null || ids.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择要封禁的用户");
        }

        // 2. 【核心安全逻辑】先查出这批 ID 对应的所有用户
        List<User> users=userMapper.selectListByIds(ids);
        if (users==null || users.isEmpty()){
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR,"未找到相关用户");
        }

        // 3. 过滤掉管理员，只留下普通用户的 ID
        List<Long> validIds=users.stream()
                .peek(user -> System.out.println("当前用户ID:" + user.getId() + ", 角色是:[" + user.getUserRole() + "]")) // 加上方括号，如果有空格一眼就能看出来
                .filter(user -> !"admin".equals(user.getUserRole()))
                .map(User::getId) //提取出这些用户的ID
                .collect(Collectors.toList());

        // 4. 如果过滤完没有合法 ID 了（比如全是管理员），直接拦截
        if (validIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能封禁管理员用户");
        }


        // 5. 【核心】使用 MyBatis-Flex 的 UpdateChain 链式更新
        // 意思就是：更新 User 表，把 is_delete 设为 1，并且 id 在这批 ids 里面
        UpdateChain.of(User.class)
                .set(User::getIsDelete,1)
                .where(USER.ID.in(validIds))
                .update();

        return ResultUtils.success(true);

    }

    /**
     * 批量解封用户
     */
    @PostMapping("/unban/batch")
    @AuthCheck(anyRole = {"admin"})
    @Operation(summary = "批量解封用户")
    public BaseResponse<Boolean> unbanUsers(@RequestBody List<Long> ids){
        // 1. 参数校验
        if (ids==null || ids.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择要解封的用户");
        }

        // 2. 【核心】使用 MyBatis-Flex 的 UpdateChain 链式更新
        // 意思就是：更新 User 表，把 is_delete 设为 0，并且 id 在这批 ids 里面
        UpdateChain.of(User.class)
                .set(User::getIsDelete,0)
                .where(USER.ID.in(ids))
                .update();

        return ResultUtils.success(true);
    }

    /**
     * 批量删除用户物理上的删除
     */
    @PostMapping("/delete/batch")
    @AuthCheck(anyRole = {"admin"})
    @Operation(summary = "批量删除用户物理上的删除")
    public BaseResponse<Boolean> deleteUsers(@RequestBody List<Long> ids){
        // 1. 参数校验
        if (ids==null || ids.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择要删除的用户");
        }

        // 1. 构建删除条件：WHERE id IN (ids)
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(User::getId).in(ids);

        // 2. 执行物理删除（底层生成 DELETE FROM user WHERE id IN (...)）
        userBaseService.removeUser(queryWrapper);

        return ResultUtils.success(true);
    }



    // ========== 用户注册与登录 接口 ==========
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
    @ApiLog
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

    /**
     * 获取当前登录用户
     *
     */
    @GetMapping("/get/login")
    @Operation(summary = "获取当前登录用户")
    @ApiLog
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request){
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request){
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 用户修改密码
     */
    @PostMapping("/updatePassword")
    @Operation(summary = "用户修改密码")
    public BaseResponse<Boolean> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpServletRequest request){
        boolean result = userService.userChangePassword(oldPassword, newPassword, request);
        return ResultUtils.success(result);
    }


    // ========== Excel导出查询结果 接口 ==========
    @GetMapping("/export")
    @Operation(summary = "导出用户数据")

    public void exportUserList(UserQueryRequest userQueryRequest, HttpServletResponse response) {
        try {
            // 1. 设置响应头，告诉浏览器这是excel文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            //防止中文乱码
            String fileName= URLEncoder.encode("用户列表", "utf-8").replaceAll("\\+", "%20");
            // 必须加上 utf-8'' 前缀，浏览器才能正确识别中文
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 2. 根据当前查询条件，查出所有符合条件的用户数据
            // 注意：导出通常不分页，所以这里需要调用一个不分页的查询方法，或者循环查出所有数据
            List<User> users=userService.listAllUsersByCondition(userQueryRequest);

            // 3. 将 User 实体转换为 UserExcelVO（脱敏，只保留需要导出的字段）
            List<UserExcelVO> userExcelVOs = users.stream()
                    .map(user -> {
                        UserExcelVO userExcelVO = new UserExcelVO();
                        userExcelVO.setUserId(user.getId());
                        userExcelVO.setUserAccount(user.getUserAccount());
                        userExcelVO.setUserName(user.getUserName());
                        userExcelVO.setUserRole(user.getUserRole());
                        userExcelVO.setCreateTime(user.getCreateTime());
                        return userExcelVO;
                    })
                    .collect(Collectors.toList());

            // 4. 一行代码写入 Excel 并响应给前端
            EasyExcel.write(response.getOutputStream(), UserExcelVO.class)
                    .sheet("用户数据") // Excel 底部的工作表名称
                    .doWrite(userExcelVOs); // 写入数据
        } catch (Exception e) {
            // 重置响应，防止前端收到一半 Excel 一半报错信息
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "导出 Excel 失败");
        }
    }

    // ========== 原有的 CRUD 接口 ==========

    /* 查询所有用户 */
    @GetMapping("/list")
    @Operation(summary = "查询所有用户")
    @AuthCheck(anyRole = {"admin", "vip"})
    @ApiLog
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
//    @PostMapping("/update")
//    @Operation(summary = "更新用户")
//    public BaseResponse<Boolean> updateUser(@RequestBody User user){
//        int rows = userBaseService.updateUser(user);
//        return ResultUtils.success(rows > 0);
//    }

    /* 删除用户 */
    @PostMapping("/delete/{id}")
    @Operation(summary = "删除用户")
    @AuthCheck(anyRole = {"admin", "vip"})
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
