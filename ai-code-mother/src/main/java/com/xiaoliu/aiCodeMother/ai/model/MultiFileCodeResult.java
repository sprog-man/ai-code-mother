package com.xiaoliu.aiCodeMother.ai.model;


import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 多文件代码生成结果
 */
@Data
public class MultiFileCodeResult
{
    @Description("HTML 代码")
    private String htmlCode;

    @Description("CSS 代码")
    private String cssCode;

    @Description("JavaScript 代码")
    private String jsCode;

    @Description("关于生成代码的说明")
    private String description;


}
