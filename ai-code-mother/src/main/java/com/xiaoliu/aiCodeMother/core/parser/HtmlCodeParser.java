package com.xiaoliu.aiCodeMother.core.parser;

import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML 代码解析器
 * 从 AI 回复中提取 ```html ... ``` 代码块
 */
public class HtmlCodeParser implements CodeParser<HtmlCodeResult>{

    /**
     * 正则表达式：匹配 ```html 代码块
     * ```html\n(任意内容)\n```  ← 注意：( ) 是捕获组
     * 第一个参数是正则表达式
     * 第二个参数 Pattern.MULTILINE 是匹配模式。它告诉正则引擎：把整个文本看作多行，遇到换行符时也会继续匹配，而不是只匹配第一行就停止。这对于提取跨越多行的代码块至关重要。
     */
    // 核心修改：在 \\s* 后面加上 ?，变成非贪婪模式
    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```+html\\n([\\s\\S]*?)\\n```", Pattern.MULTILINE);

    @Override
    public HtmlCodeResult parseCode(String aiResponse) {
        HtmlCodeResult result = new HtmlCodeResult();
        Matcher matcher = HTML_CODE_PATTERN.matcher(aiResponse);

        if (matcher.find()) {
            // 1. 提取代码
            String htmlCode = matcher.group(1).trim();
            result.setHtmlCode(htmlCode);

            // 2. 提取描述
            String description = aiResponse.substring(0, matcher.start()).trim();


            // 3. 赋值给 result
            result.setDescription(description);


        } else {
            // 【修改点 3】：兜底逻辑 - 检查是否直接以 <!DOCTYPE html> 开头
            // 忽略大小写，并且先 trim() 掉前后的空白
            if (aiResponse.trim().toUpperCase().startsWith("<!doctype html>")) {
                result.setHtmlCode(aiResponse.trim());
                result.setDescription("");
            }else {
                // 兜底逻辑
                result.setHtmlCode(aiResponse.trim());
                result.setDescription("");
            }

        }

        return result;
    }

}
