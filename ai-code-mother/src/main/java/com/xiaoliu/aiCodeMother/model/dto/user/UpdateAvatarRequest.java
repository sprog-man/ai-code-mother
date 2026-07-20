package com.xiaoliu.aiCodeMother.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新头像
 */
@Data
public class UpdateAvatarRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String newAvatarUrl;
}
