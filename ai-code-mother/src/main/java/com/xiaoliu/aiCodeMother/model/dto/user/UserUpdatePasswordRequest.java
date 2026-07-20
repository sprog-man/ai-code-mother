package com.xiaoliu.aiCodeMother.model.dto.user;

import lombok.Data;

/**
 * 用户修改密码请求
 *
 * @author xiaoliu
 */
@Data
public class UserUpdatePasswordRequest {
    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String checkPassword;

    private static final long serialVersionUID = 1L;

}
