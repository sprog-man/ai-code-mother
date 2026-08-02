package com.xiaoliu.aiCodeMother.core.parser;

import com.xiaoliu.aiCodeMother.ai.model.MultiFileCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 多文件代码解析器
 * 从 AI 回复中提取 ```html、```css、```javascript 代码块
 */
public class MultiFileCodeParser implements CodeParser<MultiFileCodeResult> {
    private static final Pattern HTML_PATTERN=Pattern.compile("```html\\n([\\s\\S]*?)\\n```",Pattern.MULTILINE);
    private static final Pattern CSS_PATTERN=Pattern.compile("```css\\n([\\s\\S]*?)\\n```",Pattern.MULTILINE);
    private static final Pattern JAVASCRIPT_PATTERN=Pattern.compile("```javascript\\n([\\s\\S]*?)\\n```",Pattern.MULTILINE);

    @Override
    public MultiFileCodeResult parseCode(String aiResponse) {
        MultiFileCodeResult result=new MultiFileCodeResult();

        result.setHtmlCode(extractCode(aiResponse, HTML_PATTERN));
        result.setCssCode(extractCode(aiResponse, CSS_PATTERN));
        result.setJsCode(extractCode(aiResponse, JAVASCRIPT_PATTERN));

        //提取代码块之外的文字作为描述
        String description=aiResponse.replaceAll("```[\\s\\S]*?```","").trim();
        result.setDescription(description);

        return result;
    }

    private String extractCode(String text,Pattern pattern){
        Matcher matcher=pattern.matcher(text);
        if (matcher.find()){
            return matcher.group(1).trim();
        }
        return "";
    }



}
