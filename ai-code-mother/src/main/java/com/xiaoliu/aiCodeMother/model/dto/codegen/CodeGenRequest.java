package com.xiaoliu.aiCodeMother.model.dto.codegen;


import lombok.Data;

/**
 * 代码生成请求
 */
@Data
public class CodeGenRequest {

    /**
     * 用户的需求描述
     */
    private String message;

    /**
     * 生成类型（html / multi_file）
     */
    private String type = "html";
}
