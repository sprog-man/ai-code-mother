package com.xiaoliu.aiCodeMother.model.dto.user;


import com.xiaoliu.aiCodeMother.model.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户分页查询请求
 *
 * @author xiaoliu
 */
@Data
@EqualsAndHashCode(callSuper = true)  // 继承父类的 equals 和 hashCode
public class UserQueryRequest extends BasePageRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 账号（模糊搜索）
     */
    private String userAccount;

    /**
     * 用户昵称（模糊搜索）
     */
    private String userName;

    /**
     * 用户角色（精确匹配）
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}
