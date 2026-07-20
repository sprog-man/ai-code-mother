package com.xiaoliu.aiCodeMother.model.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求基类
 *
 * @author xiaoliu
 */
@Data
public class BasePageRequest implements Serializable {
    /**
     * 当前页号（从 1 开始）
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     * 设置依据哪个字段进行升序或降序排列
     */
    private String sortField;

    /**
     * 排序顺序（asc 升序 / desc 降序）
     */
    private String sortOrder = "desc";

    private static final long serialVersionUID = 1L;
}
