package com.xiaoliu.aiCodeMother.service;

import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;
import com.xiaoliu.aiCodeMother.ai.model.MultiFileCodeResult;
import com.xiaoliu.aiCodeMother.core.parser.HtmlCodeParser;
import dev.langchain4j.service.SystemMessage;

/**
 * 代码生成服务
 */
public interface CodeGeneratorService {

    /**
     * 生成 单HTML 代码文件
     *
     * @param userMessage 用户的需求描述
     * @return AI 回复（包含 ```html 代码块）
     */
    @SystemMessage(fromResource = "prompts/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 生成 多文件的HTML代码
     */
    @SystemMessage(fromResource = "prompts/codegen-multi-file-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);

    /**
     * 生成 HTML 代码
     *
     * @param userMessage 用户的需求描述
     * @return AI 回复（包含 ```html 代码块）
     */
    @SystemMessage(fromResource = "prompts/codegen-simple-prompt.txt")
    String generateHtmlCodeA(String userMessage);

    /**
     * 生成 HTML 代码
     *
     * @param userMessage 用户的需求描述
     * @return AI 回复（包含 ```html 代码块）
     */
    @SystemMessage(fromResource = "prompts/codegen-example-prompt.txt")
    String generateHtmlCodeC(String userMessage);
}
