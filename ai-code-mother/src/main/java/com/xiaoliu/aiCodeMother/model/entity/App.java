package com.xiaoliu.aiCodeMother.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/* 应用实体*/
@Data
@Table("app")
public class App implements Serializable {
    /*应用ID*/
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用描述
     */
    private String appDesc;

    /**
     * 应用图标
     */
    private String appIcon;

    /**
     * 应用类型（0-AI问答 1-代码生成）
     */
    private Integer appType;

    /**
     * 审核状态（0-待审核 1-通过 2-拒绝）
     */
    private Integer reviewStatus;

    /**
     * 创建用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
