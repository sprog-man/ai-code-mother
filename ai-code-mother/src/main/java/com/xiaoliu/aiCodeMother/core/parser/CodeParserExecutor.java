package com.xiaoliu.aiCodeMother.core.parser;


import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;

/**
 * 代码解析器执行器
 */
public class CodeParserExecutor {

    /**
     * 解析 HTML 代码
     *
     * @param aiResponse AI 的完整回复
     * @return 解析后的 HTML 结果
     */
    public static HtmlCodeResult parseHtml(String aiResponse){
        CodeParser<HtmlCodeResult> parser = new HtmlCodeParser();
        return parser.parseCode(aiResponse);
    }
}
