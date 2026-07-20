package com.xiaoliu.aiCodeMother.model.dto.user;

import lombok.Data;

/**
 * 用户修改个人信息请求
 *
 * @author xiaoliu
 */
@Data
public class UserUpdateMyRequest {
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}
