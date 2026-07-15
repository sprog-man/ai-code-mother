package com.xiaoliu.aiCodeMother.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图对象
 */
@Data
public class UserVO implements Serializable {

    /* 用户ID */
    private Long id;

    /* 账号 */
    private String userAccount;

    /* 昵称 */
    private String userName;

    /* 用户头像 */
    private String userAvatar;

    /* 用户简介 */
    private  String userProfile;

    /* 用户角色 */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
