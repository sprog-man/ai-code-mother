package com.xiaoliu.aiCodeMother.model.enums;
/**
 * 代码生成类型枚举
 */
public enum CodeGenTypeEnum {

    HTML("HTML","单HTML文件"),
    MULTI_FILE("MULTI_FILE", "多文件（HTML+CSS+JS）");

    private final String value;
    private final String description;

    CodeGenTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
