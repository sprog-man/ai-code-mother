package com.xiaoliu.aiCodeMother.model.dto.user;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户请求注册
 */
@Data
public class UserRegisterRequest implements Serializable {

    /* 账号 */
    private String userAccount;

    /* 密码 */
    private String userPassword;

    /* 确认密码 */
    private String checkPassword;

    /* 用户名 */
    private String userName;

    private static final long serialVersionUID = 1L;
}
