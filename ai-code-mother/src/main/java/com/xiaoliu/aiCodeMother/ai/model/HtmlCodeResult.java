package com.xiaoliu.aiCodeMother.ai.model;



import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HTML 代码生成结果
 */
@Data
@NoArgsConstructor
public class HtmlCodeResult {

//    @Description("生成的 HTML 代码")  //`@Description` 告诉 AI 这个字段的含义，帮助 AI 生成正确的 JSON：
    private String htmlCode;

//    @Description("关于生成代码的简要说明")
    private String description;

}
